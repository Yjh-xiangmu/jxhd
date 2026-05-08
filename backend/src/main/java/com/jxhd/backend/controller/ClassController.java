package com.jxhd.backend.controller;

import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.ClassDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.ClassService;
import com.jxhd.backend.vo.ClassVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    /** 查询班级列表 GET /api/classes?keyword=&status= */
    @GetMapping
    public Result<List<ClassVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(classService.listAll(keyword, status));
    }

    /** 新增班级 */
    @PostMapping
    public Result<Void> add(@RequestBody ClassDTO dto) {
        classService.add(dto);
        return Result.success();
    }

    /** 修改班级 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassDTO dto) {
        dto.setId(id);
        classService.update(dto);
        return Result.success();
    }

    /** 启用/禁用 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        classService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    /** 删除班级 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classService.delete(id);
        return Result.success();
    }

    /** 获取所有可选教师（用于分配班主任下拉） */
    @GetMapping("/teachers")
    public Result<List<User>> teachers() {
        return Result.success(classService.listTeachers());
    }
}