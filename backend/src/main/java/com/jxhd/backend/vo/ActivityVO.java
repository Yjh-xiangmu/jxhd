package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityVO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime activityTime;
    private LocalDateTime signupDeadline;
    private Integer maxCount;
    private Integer status;
    private Long authorId;
    private String authorName;
    private Integer signupCount;
    private LocalDateTime createTime;
}
