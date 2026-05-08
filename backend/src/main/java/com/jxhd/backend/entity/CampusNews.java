package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("campus_news")
public class CampusNews {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String type;
    private String content;
    private Integer status;
    private Long authorId;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
