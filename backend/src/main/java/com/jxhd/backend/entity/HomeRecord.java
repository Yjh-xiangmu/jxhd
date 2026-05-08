package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("home_record")
public class HomeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long parentId;
    private String content;
    private LocalDate recordDate;
    private LocalDateTime createTime;
}
