package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentVO {
    private Long id;
    private String name;
    private Integer gender;
    private String genderLabel;   // 男/女
    private LocalDate birthday;
    private Long classId;
    private String className;     // 班级名称
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
}