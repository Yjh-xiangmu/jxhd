package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.ParentBindDTO;
import com.jxhd.backend.dto.StudentDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.LogService;
import com.jxhd.backend.service.StudentService;
import com.jxhd.backend.vo.StudentDetailVO;
import com.jxhd.backend.vo.StudentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final LogService     logService;

    @GetMapping
    public Result<IPage<StudentVO>> list(
            @RequestParam(defaultValue = "1")  int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false)    Long classId,
            @RequestParam(required = false)    String keyword,
            @RequestParam(required = false)    Integer status) {
        return Result.success(studentService.page(pageNum, pageSize, classId, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<StudentDetailVO> detail(@PathVariable Long id) {
        return Result.success(studentService.detail(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody StudentDTO dto, HttpSession session, HttpServletRequest request) {
        studentService.add(dto);
        User user = (User) session.getAttribute("currentUser");
        logService.record(user, "学生管理", "新增学生", "新增学生：" + dto.getName(), request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StudentDTO dto,
                               HttpSession session, HttpServletRequest request) {
        dto.setId(id);
        studentService.update(dto);
        User user = (User) session.getAttribute("currentUser");
        logService.record(user, "学生管理", "修改学生", "修改学生ID：" + id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        studentService.delete(id);
        User user = (User) session.getAttribute("currentUser");
        logService.record(user, "学生管理", "删除学生", "删除学生ID：" + id, request);
        return Result.success();
    }

    @PutMapping("/{id}/change-class")
    public Result<Void> changeClass(@PathVariable Long id, @RequestBody Map<String, Long> body,
                                    HttpSession session, HttpServletRequest request) {
        studentService.changeClass(id, body.get("classId"));
        User user = (User) session.getAttribute("currentUser");
        logService.record(user, "学生管理", "调班", "学生ID：" + id + " 调入班级ID：" + body.get("classId"), request);
        return Result.success();
    }

    @PostMapping("/bind-parent")
    public Result<Void> bindParent(@RequestBody ParentBindDTO dto) {
        studentService.bindParent(dto);
        return Result.success();
    }

    @DeleteMapping("/unbind-parent/{bindId}")
    public Result<Void> unbindParent(@PathVariable Long bindId) {
        studentService.unbindParent(bindId);
        return Result.success();
    }

    @PutMapping("/audit-bind/{bindId}")
    public Result<Void> auditBind(@PathVariable Long bindId, @RequestBody Map<String, Object> body) {
        Integer status    = (Integer) body.get("status");
        Long    auditorId = Long.valueOf(body.get("auditorId").toString());
        studentService.auditBind(bindId, status, auditorId);
        return Result.success();
    }
}
