package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MySignupVO {
    private Long signupId;
    private Long activityId;
    private String activityTitle;
    private String activityLocation;
    private LocalDateTime activityTime;
    private Integer activityStatus;
    private String studentName;
    private String remark;
    private LocalDateTime signupTime;
}
