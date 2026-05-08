package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.UserDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 分页列表 GET /api/users?pageNum=1&pageSize=10&role=teacher&keyword=xxx */
    @GetMapping
    public Result<IPage<User>> list(
            @RequestParam(defaultValue = "1")  int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false)    String role,
            @RequestParam(required = false)    String keyword) {
        return Result.success(userService.page(pageNum, pageSize, role, keyword));
    }

    /** 新增用户 */
    @PostMapping
    public Result<Void> add(@RequestBody UserDTO dto) {
        userService.add(dto);
        return Result.success();
    }

    /** 修改用户 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        dto.setId(id);
        userService.update(dto);
        return Result.success();
    }

    /** 启用/禁用  PUT /api/users/{id}/status  body: {"status": 0} */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    /** 重置密码 */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
