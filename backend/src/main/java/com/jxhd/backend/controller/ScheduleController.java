package com.jxhd.backend.controller;

import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.ScheduleDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.ScheduleService;
import com.jxhd.backend.vo.ClassVO;
import com.jxhd.backend.vo.ScheduleVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /** 按班级查课表 */
    @GetMapping
    public Result<List<ScheduleVO>> list(@RequestParam Long classId) {
        return Result.success(scheduleService.getByClass(classId));
    }

    /** 保存课表条目（新增或更新） */
    @PostMapping
    public Result<Void> save(@RequestBody ScheduleDTO dto) {
        scheduleService.saveEntry(dto);
        return Result.success();
    }

    /** 删除课表条目 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scheduleService.deleteEntry(id);
        return Result.success();
    }

    /** 获取当前教师的主要班级信息 */
    @GetMapping("/my-class")
    public Result<ClassVO> myClass(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");
        ClassVO cls = scheduleService.getTeacherClass(user.getId());
        if (cls == null) return Result.error("当前账号尚未分配班级");
        return Result.success(cls);
    }
}
