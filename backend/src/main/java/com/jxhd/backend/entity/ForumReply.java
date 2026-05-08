package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("forum_reply")
public class ForumReply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long authorId;
    private String authorRole;
    private String content;
    private LocalDateTime createTime;
}
