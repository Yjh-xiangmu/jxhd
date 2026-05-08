package com.jxhd.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime activityTime;
    private LocalDateTime signupDeadline;
    private Integer maxCount;
    private Integer status;
}
