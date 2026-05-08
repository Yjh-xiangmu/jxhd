package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.entity.*;
import com.jxhd.backend.mapper.*;
import com.jxhd.backend.vo.PendingBindVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherClassMapper teacherClassMapper;
    private final StudentMapper studentMapper;
    private final ParentStudentMapper parentStudentMapper;
    private final UserMapper userMapper;

    /**
     * 获取当前教师班级内所有待审核的家长绑定申请
     */
    @GetMapping("/pending-binds")
    public Result<List<PendingBindVO>> pendingBinds(HttpSession session) {
        User teacher = (User) session.getAttribute("currentUser");
        if (teacher == null) return Result.error(401, "未登录");

        // 找教师的主班级（优先 is_head=1）
        List<TeacherClass> tcList = teacherClassMapper.selectList(
                new LambdaQueryWrapper<TeacherClass>()
                        .eq(TeacherClass::getTeacherId, teacher.getId())
                        .orderByDesc(TeacherClass::getIsHead));
        if (tcList.isEmpty()) return Result.success(List.of());

        Long classId = tcList.get(0).getClassId();

        // 找该班所有在园幼儿
        List<Student> students = studentMapper.selectList(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getClassId, classId)
                        .eq(Student::getStatus, 1));
        if (students.isEmpty()) return Result.success(List.of());

        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
        Map<Long, Student> studentMap = students.stream().collect(Collectors.toMap(Student::getId, s -> s));

        // 查待审核绑定（status=0）
        List<ParentStudent> pending = parentStudentMapper.selectList(
                new LambdaQueryWrapper<ParentStudent>()
                        .in(ParentStudent::getStudentId, studentIds)
                        .eq(ParentStudent::getStatus, 0)
                        .orderByAsc(ParentStudent::getApplyTime));
        if (pending.isEmpty()) return Result.success(List.of());

        // 批量加载家长信息
        List<Long> parentIds = pending.stream().map(ParentStudent::getParentId).distinct().collect(Collectors.toList());
        Map<Long, User> parentMap = userMapper.selectBatchIds(parentIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<PendingBindVO> result = pending.stream().map(p -> {
            PendingBindVO vo = new PendingBindVO();
            vo.setBindId(p.getId());
            vo.setStudentId(p.getStudentId());
            Student stu = studentMap.get(p.getStudentId());
            vo.setStudentName(stu != null ? stu.getName() : "-");
            vo.setParentId(p.getParentId());
            User parent = parentMap.get(p.getParentId());
            if (parent != null) {
                vo.setParentName(StringUtils.hasText(parent.getRealName()) ? parent.getRealName() : parent.getUsername());
                vo.setParentUsername(parent.getUsername());
                vo.setParentPhone(parent.getPhone());
            }
            vo.setRelation(p.getRelation());
            vo.setApplyTime(p.getApplyTime());
            return vo;
        }).collect(Collectors.toList());

        return Result.success(result);
    }
}
