package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.entity.ForumPost;
import com.jxhd.backend.entity.ForumReply;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.ForumPostMapper;
import com.jxhd.backend.mapper.ForumReplyMapper;
import com.jxhd.backend.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ForumController {

    private final ForumPostMapper postMapper;
    private final ForumReplyMapper replyMapper;
    private final UserMapper userMapper;

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    @Data
    static class PostVO {
        private Long id;
        private Long authorId;
        private String authorName;
        private String authorRole;
        private String title;
        private String content;
        private java.util.List<String> images;
        private Integer isPinned;
        private Integer replyCount;
        private LocalDateTime createTime;
    }

    @Data
    static class ReplyVO {
        private Long id;
        private Long postId;
        private Long authorId;
        private String authorName;
        private String authorRole;
        private String content;
        private LocalDateTime createTime;
    }

    // ── 图片上传 ──────────────────────────────────────────────────────────────

    @PostMapping("/upload-image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        if (session.getAttribute("currentUser") == null) return Result.error(401, "未登录");
        if (file.isEmpty()) return Result.error(400, "文件为空");
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.')) : ".jpg";
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        String absDir = Paths.get(System.getProperty("user.dir"), uploadPath).toAbsolutePath().toString();
        File dir = new File(absDir);
        if (!dir.exists()) dir.mkdirs();
        try {
            file.transferTo(new File(dir, filename));
        } catch (IOException e) {
            return Result.error(500, "文件保存失败: " + e.getMessage());
        }
        return Result.success("/uploads/" + filename);
    }

    // ── 帖子列表（分页）──────────────────────────────────────────────────────

    @GetMapping("/posts")
    public Result<IPage<PostVO>> posts(@RequestParam(defaultValue = "1")  int pageNum,
                                       @RequestParam(defaultValue = "20") int pageSize,
                                       @RequestParam(required = false)    String keyword) {
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<ForumPost>()
                .like(StringUtils.hasText(keyword), ForumPost::getTitle, keyword)
                .orderByDesc(ForumPost::getIsPinned)
                .orderByDesc(ForumPost::getCreateTime);

        IPage<ForumPost> page = postMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<Long> authorIds = page.getRecords().stream().map(ForumPost::getAuthorId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = authorIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(authorIds).stream().collect(Collectors.toMap(User::getId, u -> u));

        IPage<PostVO> result = page.convert(p -> toPostVO(p, userMap));
        return Result.success(result);
    }

    // ── 帖子详情 + 所有回复 ───────────────────────────────────────────────────

    @GetMapping("/posts/{id}")
    public Result<Map<String, Object>> postDetail(@PathVariable Long id) {
        ForumPost post = postMapper.selectById(id);
        if (post == null) return Result.error(404, "帖子不存在");

        List<ForumReply> replies = replyMapper.selectList(
                new LambdaQueryWrapper<ForumReply>()
                        .eq(ForumReply::getPostId, id)
                        .orderByAsc(ForumReply::getCreateTime));

        List<Long> allAuthorIds = new ArrayList<>();
        allAuthorIds.add(post.getAuthorId());
        replies.forEach(r -> allAuthorIds.add(r.getAuthorId()));
        Map<Long, User> userMap = userMapper.selectBatchIds(allAuthorIds.stream().distinct().collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(User::getId, u -> u));

        List<ReplyVO> replyVOs = replies.stream().map(r -> toReplyVO(r, userMap)).collect(Collectors.toList());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("post", toPostVO(post, userMap));
        data.put("replies", replyVOs);
        return Result.success(data);
    }

    // ── 发帖 ─────────────────────────────────────────────────────────────────

    @PostMapping("/posts")
    public Result<Void> addPost(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = getUser(session);
        if (user == null) return Result.error(401, "未登录");

        String title   = body.getOrDefault("title", "").toString().trim();
        String content = body.getOrDefault("content", "").toString().trim();
        if (!StringUtils.hasText(title))   return Result.error(400, "标题不能为空");
        if (!StringUtils.hasText(content)) return Result.error(400, "内容不能为空");

        @SuppressWarnings("unchecked")
        List<String> images = body.get("images") instanceof List ? (List<String>) body.get("images") : List.of();

        ForumPost post = new ForumPost();
        post.setAuthorId(user.getId());
        post.setAuthorRole(user.getRole());
        post.setTitle(title);
        post.setContent(content);
        post.setImages(images.isEmpty() ? null : images);
        post.setIsPinned(0);
        post.setReplyCount(0);
        post.setCreateTime(LocalDateTime.now());
        postMapper.insert(post);
        return Result.success(null);
    }

    // ── 删除帖子（本人或管理员）────────────────────────────────────────────────

    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(@PathVariable Long id, HttpSession session) {
        User user = getUser(session);
        if (user == null) return Result.error(401, "未登录");
        ForumPost post = postMapper.selectById(id);
        if (post == null) return Result.error(404, "帖子不存在");
        if (!post.getAuthorId().equals(user.getId()) && !"admin".equals(user.getRole()))
            return Result.error(403, "无权删除");
        replyMapper.delete(new LambdaQueryWrapper<ForumReply>().eq(ForumReply::getPostId, id));
        postMapper.deleteById(id);
        return Result.success(null);
    }

    // ── 置顶/取消置顶（仅管理员）──────────────────────────────────────────────

    @PutMapping("/posts/{id}/pin")
    public Result<Void> togglePin(@PathVariable Long id, HttpSession session) {
        User user = getUser(session);
        if (user == null) return Result.error(401, "未登录");
        if (!"admin".equals(user.getRole())) return Result.error(403, "无权操作");
        ForumPost post = postMapper.selectById(id);
        if (post == null) return Result.error(404, "帖子不存在");
        post.setIsPinned(post.getIsPinned() == 1 ? 0 : 1);
        postMapper.updateById(post);
        return Result.success(null);
    }

    // ── 回复 ─────────────────────────────────────────────────────────────────

    @PostMapping("/posts/{id}/replies")
    public Result<Void> addReply(@PathVariable Long id,
                                  @RequestBody Map<String, String> body,
                                  HttpSession session) {
        User user = getUser(session);
        if (user == null) return Result.error(401, "未登录");

        String content = body.getOrDefault("content", "").trim();
        if (!StringUtils.hasText(content)) return Result.error(400, "回复内容不能为空");

        ForumPost post = postMapper.selectById(id);
        if (post == null) return Result.error(404, "帖子不存在");

        ForumReply reply = new ForumReply();
        reply.setPostId(id);
        reply.setAuthorId(user.getId());
        reply.setAuthorRole(user.getRole());
        reply.setContent(content);
        reply.setCreateTime(LocalDateTime.now());
        replyMapper.insert(reply);

        // 更新回复数冗余字段
        post.setReplyCount(post.getReplyCount() + 1);
        postMapper.updateById(post);
        return Result.success(null);
    }

    // ── 删除回复 ──────────────────────────────────────────────────────────────

    @DeleteMapping("/replies/{id}")
    public Result<Void> deleteReply(@PathVariable Long id, HttpSession session) {
        User user = getUser(session);
        if (user == null) return Result.error(401, "未登录");
        ForumReply reply = replyMapper.selectById(id);
        if (reply == null) return Result.error(404, "回复不存在");
        if (!reply.getAuthorId().equals(user.getId()) && !"admin".equals(user.getRole()))
            return Result.error(403, "无权删除");

        replyMapper.deleteById(id);
        ForumPost post = postMapper.selectById(reply.getPostId());
        if (post != null && post.getReplyCount() > 0) {
            post.setReplyCount(post.getReplyCount() - 1);
            postMapper.updateById(post);
        }
        return Result.success(null);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    private String displayName(User u) {
        if (u == null) return "未知用户";
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

    private PostVO toPostVO(ForumPost p, Map<Long, User> userMap) {
        PostVO vo = new PostVO();
        vo.setId(p.getId());
        vo.setAuthorId(p.getAuthorId());
        User u = userMap.get(p.getAuthorId());
        vo.setAuthorName(displayName(u));
        vo.setAuthorRole(roleLabel(p.getAuthorRole()));
        vo.setTitle(p.getTitle());
        vo.setContent(p.getContent());
        vo.setImages(p.getImages());
        vo.setIsPinned(p.getIsPinned());
        vo.setReplyCount(p.getReplyCount());
        vo.setCreateTime(p.getCreateTime());
        return vo;
    }

    private ReplyVO toReplyVO(ForumReply r, Map<Long, User> userMap) {
        ReplyVO vo = new ReplyVO();
        vo.setId(r.getId());
        vo.setPostId(r.getPostId());
        vo.setAuthorId(r.getAuthorId());
        User u = userMap.get(r.getAuthorId());
        vo.setAuthorName(displayName(u));
        vo.setAuthorRole(roleLabel(r.getAuthorRole()));
        vo.setContent(r.getContent());
        vo.setCreateTime(r.getCreateTime());
        return vo;
    }
}
