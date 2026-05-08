package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.entity.*;
import com.jxhd.backend.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class DashboardController {

    private final UserMapper userMapper;
    private final ClassMapper classMapper;
    private final StudentMapper studentMapper;
    private final ParentStudentMapper parentStudentMapper;
    private final AttendanceRecordMapper attendanceMapper;
    private final CampusActivityMapper activityMapper;
    private final GrowthRecordMapper growthRecordMapper;
    private final ForumPostMapper forumPostMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        long teacherCount  = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "teacher").eq(User::getStatus, 1));
        long parentCount   = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "parent").eq(User::getStatus, 1));
        long classCount    = classMapper.selectCount(new LambdaQueryWrapper<SysClass>().eq(SysClass::getStatus, 1));
        long studentCount  = studentMapper.selectCount(new LambdaQueryWrapper<Student>().eq(Student::getStatus, 1));
        long pendingBinds  = parentStudentMapper.selectCount(new LambdaQueryWrapper<ParentStudent>().eq(ParentStudent::getStatus, 0));
        long openActivities = activityMapper.selectCount(new LambdaQueryWrapper<CampusActivity>().eq(CampusActivity::getStatus, 1));
        long growthToday   = growthRecordMapper.selectCount(
                new LambdaQueryWrapper<GrowthRecord>()
                        .ge(GrowthRecord::getCreateTime, LocalDate.now().atStartOfDay()));
        long forumPosts    = forumPostMapper.selectCount(null);

        // 今日出勤
        long presentToday = attendanceMapper.selectCount(
                new LambdaQueryWrapper<AttendanceRecord>()
                        .eq(AttendanceRecord::getDate, LocalDate.now())
                        .eq(AttendanceRecord::getStatus, 1));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("teacherCount",   teacherCount);
        data.put("parentCount",    parentCount);
        data.put("classCount",     classCount);
        data.put("studentCount",   studentCount);
        data.put("pendingBinds",   pendingBinds);
        data.put("openActivities", openActivities);
        data.put("growthToday",    growthToday);
        data.put("forumPosts",     forumPosts);
        data.put("presentToday",   presentToday);

        return Result.success(data);
    }
}
