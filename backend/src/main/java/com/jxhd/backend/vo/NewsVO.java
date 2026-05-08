package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsVO {
    private Long id;
    private String title;
    private String type;
    private String content;
    private Integer status;
    private Long authorId;
    private String authorName;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
