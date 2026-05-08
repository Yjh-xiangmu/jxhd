package com.jxhd.backend.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;
}