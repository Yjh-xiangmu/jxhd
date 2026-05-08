package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "forum_post", autoResultMap = true)
public class ForumPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private String authorRole;
    private String title;
    private String content;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;

    private Integer isPinned;
    private Integer replyCount;
    private LocalDateTime createTime;
}
