package com.jxhd.backend.controller;

import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.LoginDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.UserService;
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

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto, HttpSession session) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        // 将用户信息存入Session
        session.setAttribute("currentUser", user);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("sessionId", session.getId());
        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    @GetMapping("/current")
    public Result<User> current(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(user);
    }
}
