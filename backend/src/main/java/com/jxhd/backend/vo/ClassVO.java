package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassVO {
    private Long id;
    private String className;
    private String grade;
    private Long headTeacherId;
    private String headTeacherName;   // 班主任姓名
    private String semester;
    private Integer status;
    private Integer studentCount;     // 在园幼儿数
    private LocalDateTime createTime;
}