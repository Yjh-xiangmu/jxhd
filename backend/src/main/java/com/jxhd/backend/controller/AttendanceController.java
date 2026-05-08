package com.jxhd.backend.controller;

import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.AttendanceDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.AttendanceService;
import com.jxhd.backend.vo.AttendanceVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /** 获取某班某天考勤（管理员/教师均可用） */
    @GetMapping
    public Result<List<AttendanceVO>> list(
            @RequestParam Long classId,
            @RequestParam String date) {
        return Result.success(attendanceService.getDailyAttendance(classId, date));
    }

    /** 记录/更新单个学生考勤 */
    @PostMapping
    public Result<Void> record(@RequestBody AttendanceDTO dto, HttpSession session) {
        Long recorderId = getCurrentUserId(session);
        attendanceService.recordAttendance(dto, recorderId);
        return Result.success();
    }

    /** 一键全勤（批量标记出勤） */
    @PostMapping("/batch-present")
    public Result<Void> batchPresent(@RequestBody AttendanceDTO dto, HttpSession session) {
        Long recorderId = getCurrentUserId(session);
        attendanceService.batchPresent(dto.getClassId(), dto.getDate(), dto.getStudentIds(), recorderId);
        return Result.success();
    }

    private Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null ? user.getId() : 1L;
    }
}
