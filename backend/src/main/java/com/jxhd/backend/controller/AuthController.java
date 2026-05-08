package com.jxhd.backend.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin // 允许前端跨域访问
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", loginUser.getUsername())
                .eq("password", loginUser.getPassword());

        User user = userMapper.selectOne(query);

        if (user != null) {
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("data", user);
        } else {
            result.put("code", 400);
            result.put("msg", "账号或密码错误");
        }
        return result;
    }
}