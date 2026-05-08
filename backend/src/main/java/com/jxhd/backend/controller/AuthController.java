package com.jxhd.backend.controller;

import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.LoginDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.LogService;
import com.jxhd.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final LogService  logService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto, HttpSession session, HttpServletRequest request) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        session.setAttribute("currentUser", user);
        logService.record(user, "登录", "登录系统", "用户登录", request);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("sessionId", session.getId());
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute("currentUser");
        logService.record(user, "登录", "退出系统", "用户登出", request);
        session.invalidate();
        return Result.success();
    }

    @GetMapping("/current")
    public Result<User> current(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");
        return Result.success(user);
    }
}
