package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SignupVO {
    private Long id;
    private Long parentId;
    private String parentName;
    private String parentPhone;
    private Long studentId;
    private String studentName;
    private String className;
    private String remark;
    private LocalDateTime createTime;
}
