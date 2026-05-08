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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parent")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ParentController {

    private final ParentStudentMapper parentStudentMapper;
    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;
    private final HomeRecordMapper homeRecordMapper;

    @Data
    static class ChildVO {
        private Long studentId;
        private String studentName;
        private Integer gender;
        private String className;
        private Long classId;
    }

    /**
     * 获取家长已绑定且通过审核的孩子列表
     */
    @GetMapping("/my-children")
    public Result<List<ChildVO>> myChildren(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");

        List<ParentStudent> binds = parentStudentMapper.selectList(
                new LambdaQueryWrapper<ParentStudent>()
                        .eq(ParentStudent::getParentId, user.getId())
                        .eq(ParentStudent::getStatus, 1));

        if (binds.isEmpty()) return Result.success(List.of());

        List<Long> studentIds = binds.stream().map(ParentStudent::getStudentId).collect(Collectors.toList());
        Map<Long, Student> studentMap = studentMapper.selectBatchIds(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, s -> s));

        List<Long> classIds = studentMap.values().stream()
                .filter(s -> s.getClassId() != null).map(Student::getClassId).distinct().collect(Collectors.toList());
        Map<Long, String> classNameMap = classIds.isEmpty() ? Map.of() :
                classMapper.selectBatchIds(classIds).stream()
                        .collect(Collectors.toMap(SysClass::getId, SysClass::getClassName));

        List<ChildVO> result = studentIds.stream().map(sid -> {
            Student s = studentMap.get(sid);
            if (s == null) return null;
            ChildVO vo = new ChildVO();
            vo.setStudentId(sid);
            vo.setStudentName(s.getName());
            vo.setGender(s.getGender());
            vo.setClassId(s.getClassId());
            vo.setClassName(s.getClassId() != null ? classNameMap.getOrDefault(s.getClassId(), "未知") : "未分班");
            return vo;
        }).filter(v -> v != null).collect(Collectors.toList());

        return Result.success(result);
    }

    // ── 在家表现记录 ───────────────────────────────────────────────────────────

    @Data
    static class HomeRecordVO {
        private Long id;
        private Long studentId;
        private String studentName;
        private String content;
        private LocalDate recordDate;
        private LocalDateTime createTime;
    }

    /** 查询某孩子的在家表现记录 */
    @GetMapping("/home-records/{studentId}")
    public Result<List<HomeRecordVO>> homeRecords(@PathVariable Long studentId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");

        List<HomeRecord> records = homeRecordMapper.selectList(
                new LambdaQueryWrapper<HomeRecord>()
                        .eq(HomeRecord::getStudentId, studentId)
                        .eq(HomeRecord::getParentId, user.getId())
                        .orderByDesc(HomeRecord::getRecordDate));

        Student s = studentMapper.selectById(studentId);
        String studentName = s != null ? s.getName() : "-";

        List<HomeRecordVO> result = records.stream().map(r -> {
            HomeRecordVO vo = new HomeRecordVO();
            vo.setId(r.getId());
            vo.setStudentId(r.getStudentId());
            vo.setStudentName(studentName);
            vo.setContent(r.getContent());
            vo.setRecordDate(r.getRecordDate());
            vo.setCreateTime(r.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    /** 添加在家表现记录 */
    @PostMapping("/home-records")
    public Result<Void> addHomeRecord(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");

        Long studentId = Long.valueOf(body.get("studentId").toString());
        String content = body.getOrDefault("content", "").toString();
        if (!StringUtils.hasText(content)) return Result.error(400, "内容不能为空");

        Object dateObj = body.get("recordDate");
        LocalDate recordDate = (dateObj != null && !dateObj.toString().isEmpty())
                ? LocalDate.parse(dateObj.toString()) : LocalDate.now();

        HomeRecord r = new HomeRecord();
        r.setStudentId(studentId);
        r.setParentId(user.getId());
        r.setContent(content);
        r.setRecordDate(recordDate);
        r.setCreateTime(LocalDateTime.now());
        homeRecordMapper.insert(r);
        return Result.success(null);
    }

    /** 删除在家表现记录 */
    @DeleteMapping("/home-records/{id}")
    public Result<Void> deleteHomeRecord(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");
        HomeRecord r = homeRecordMapper.selectById(id);
        if (r == null) return Result.error(404, "记录不存在");
        if (!r.getParentId().equals(user.getId())) return Result.error(403, "无权删除");
        homeRecordMapper.deleteById(id);
        return Result.success(null);
    }
}
