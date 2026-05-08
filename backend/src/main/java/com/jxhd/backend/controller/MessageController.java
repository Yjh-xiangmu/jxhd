package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.entity.*;
import com.jxhd.backend.mapper.*;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class MessageController {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ParentStudentMapper parentStudentMapper;
    private final TeacherClassMapper teacherClassMapper;
    private final StudentMapper studentMapper;

    @Data
    static class ConversationVO {
        private Long peerId;
        private String peerName;
        private String peerRole;
        private String lastMessage;
        private LocalDateTime lastTime;
        private long unreadCount;
    }

    @Data
    static class MessageVO {
        private Long id;
        private Long fromId;
        private Long toId;
        private String content;
        private Integer isRead;
        private LocalDateTime createTime;
        private Boolean isMine;
    }

    @Data
    static class ContactVO {
        private Long id;
        private String name;
        private String role;
    }

    // ── 会话列表（我参与过的所有对话，取最新一条） ──────────────────────────────

    @GetMapping("/conversations")
    public Result<List<ConversationVO>> conversations(HttpSession session) {
        User me = getUser(session);
        if (me == null) return Result.error(401, "未登录");

        // 找所有与我有过消息往来的 peerIds
        List<Message> sent = messageMapper.selectList(
                new LambdaQueryWrapper<Message>().eq(Message::getFromId, me.getId()));
        List<Message> recv = messageMapper.selectList(
                new LambdaQueryWrapper<Message>().eq(Message::getToId, me.getId()));

        Set<Long> peerIds = new LinkedHashSet<>();
        sent.forEach(m -> peerIds.add(m.getToId()));
        recv.forEach(m -> peerIds.add(m.getFromId()));

        if (peerIds.isEmpty()) return Result.success(List.of());

        Map<Long, User> peerMap = userMapper.selectBatchIds(peerIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<ConversationVO> result = peerIds.stream().map(peerId -> {
            // 该会话内所有消息按时间降序
            List<Message> all = messageMapper.selectList(
                    new LambdaQueryWrapper<Message>()
                            .and(w -> w.eq(Message::getFromId, me.getId()).eq(Message::getToId, peerId)
                                    .or().eq(Message::getFromId, peerId).eq(Message::getToId, me.getId()))
                            .orderByDesc(Message::getCreateTime));

            if (all.isEmpty()) return null;

            long unread = all.stream()
                    .filter(m -> m.getToId().equals(me.getId()) && m.getIsRead() == 0)
                    .count();

            ConversationVO vo = new ConversationVO();
            vo.setPeerId(peerId);
            User peer = peerMap.get(peerId);
            vo.setPeerName(peer != null ? displayName(peer) : "未知用户");
            vo.setPeerRole(peer != null ? roleLabel(peer.getRole()) : "-");
            vo.setLastMessage(all.get(0).getContent());
            vo.setLastTime(all.get(0).getCreateTime());
            vo.setUnreadCount(unread);
            return vo;
        }).filter(Objects::nonNull)
          .sorted(Comparator.comparing(ConversationVO::getLastTime).reversed())
          .collect(Collectors.toList());

        return Result.success(result);
    }

    // ── 获取与某人的消息历史 ─────────────────────────────────────────────────

    @GetMapping("/thread/{peerId}")
    public Result<List<MessageVO>> thread(@PathVariable Long peerId, HttpSession session) {
        User me = getUser(session);
        if (me == null) return Result.error(401, "未登录");

        List<Message> msgs = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .and(w -> w.eq(Message::getFromId, me.getId()).eq(Message::getToId, peerId)
                                .or().eq(Message::getFromId, peerId).eq(Message::getToId, me.getId()))
                        .orderByAsc(Message::getCreateTime));

        // 标记已读
        msgs.stream()
            .filter(m -> m.getToId().equals(me.getId()) && m.getIsRead() == 0)
            .forEach(m -> { m.setIsRead(1); messageMapper.updateById(m); });

        List<MessageVO> vos = msgs.stream().map(m -> {
            MessageVO vo = new MessageVO();
            vo.setId(m.getId());
            vo.setFromId(m.getFromId());
            vo.setToId(m.getToId());
            vo.setContent(m.getContent());
            vo.setIsRead(m.getIsRead());
            vo.setCreateTime(m.getCreateTime());
            vo.setIsMine(m.getFromId().equals(me.getId()));
            return vo;
        }).collect(Collectors.toList());

        return Result.success(vos);
    }

    // ── 发送消息 ─────────────────────────────────────────────────────────────

    @PostMapping("/send")
    public Result<Void> send(@RequestBody Map<String, Object> body, HttpSession session) {
        User me = getUser(session);
        if (me == null) return Result.error(401, "未登录");

        Long toId = Long.valueOf(body.get("toId").toString());
        String content = body.getOrDefault("content", "").toString().trim();
        if (!StringUtils.hasText(content)) return Result.error(400, "消息不能为空");

        Message msg = new Message();
        msg.setFromId(me.getId());
        msg.setToId(toId);
        msg.setContent(content);
        msg.setIsRead(0);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
        return Result.success(null);
    }

    // ── 未读消息数 ────────────────────────────────────────────────────────────

    @GetMapping("/unread-count")
    public Result<Long> unreadCount(HttpSession session) {
        User me = getUser(session);
        if (me == null) return Result.error(401, "未登录");
        long count = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getToId, me.getId())
                        .eq(Message::getIsRead, 0));
        return Result.success(count);
    }

    // ── 可联系人列表（教师返回班级家长，家长返回孩子班主任） ──────────────────

    @GetMapping("/contacts")
    public Result<List<ContactVO>> contacts(HttpSession session) {
        User me = getUser(session);
        if (me == null) return Result.error(401, "未登录");

        List<Long> contactIds = new ArrayList<>();

        if ("teacher".equals(me.getRole())) {
            // 找教师的班级
            List<TeacherClass> tcs = teacherClassMapper.selectList(
                    new LambdaQueryWrapper<TeacherClass>().eq(TeacherClass::getTeacherId, me.getId()));
            if (!tcs.isEmpty()) {
                Long classId = tcs.stream()
                        .max(Comparator.comparingInt(tc -> tc.getIsHead() != null ? tc.getIsHead() : 0))
                        .get().getClassId();
                // 班级所有在园学生的家长（已通过绑定）
                List<Student> students = studentMapper.selectList(
                        new LambdaQueryWrapper<Student>().eq(Student::getClassId, classId).eq(Student::getStatus, 1));
                if (!students.isEmpty()) {
                    List<Long> stuIds = students.stream().map(Student::getId).collect(Collectors.toList());
                    parentStudentMapper.selectList(
                                    new LambdaQueryWrapper<ParentStudent>()
                                            .in(ParentStudent::getStudentId, stuIds)
                                            .eq(ParentStudent::getStatus, 1))
                            .forEach(ps -> contactIds.add(ps.getParentId()));
                }
            }
        } else if ("parent".equals(me.getRole())) {
            // 找孩子所在班级的教师
            List<ParentStudent> binds = parentStudentMapper.selectList(
                    new LambdaQueryWrapper<ParentStudent>()
                            .eq(ParentStudent::getParentId, me.getId())
                            .eq(ParentStudent::getStatus, 1));
            Set<Long> classIds = new HashSet<>();
            if (!binds.isEmpty()) {
                List<Long> stuIds = binds.stream().map(ParentStudent::getStudentId).collect(Collectors.toList());
                studentMapper.selectBatchIds(stuIds).stream()
                        .filter(s -> s.getClassId() != null)
                        .forEach(s -> classIds.add(s.getClassId()));
            }
            if (!classIds.isEmpty()) {
                teacherClassMapper.selectList(
                                new LambdaQueryWrapper<TeacherClass>().in(TeacherClass::getClassId, classIds))
                        .forEach(tc -> contactIds.add(tc.getTeacherId()));
            }
        }

        if (contactIds.isEmpty()) return Result.success(List.of());

        List<ContactVO> result = userMapper.selectBatchIds(contactIds.stream().distinct().collect(Collectors.toList()))
                .stream().map(u -> {
                    ContactVO vo = new ContactVO();
                    vo.setId(u.getId());
                    vo.setName(displayName(u));
                    vo.setRole(roleLabel(u.getRole()));
                    return vo;
                }).collect(Collectors.toList());

        return Result.success(result);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    private String displayName(User u) {
        return StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername();
    }

    private String roleLabel(String role) {
        return switch (role) {
            case "admin"   -> "管理员";
            case "teacher" -> "老师";
            case "parent"  -> "家长";
            default        -> role;
        };
    }
}
