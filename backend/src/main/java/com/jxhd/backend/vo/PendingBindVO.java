package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PendingBindVO {
    private Long bindId;
    private Long studentId;
    private String studentName;
    private Long parentId;
    private String parentName;
    private String parentUsername;
    private String parentPhone;
    private String relation;
    private LocalDateTime applyTime;
}
