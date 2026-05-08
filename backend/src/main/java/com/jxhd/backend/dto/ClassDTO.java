package com.jxhd.backend.dto;

import lombok.Data;

@Data
public class ClassDTO {
    private Long id;
    private String className;
    private String grade;
    private Long headTeacherId;
    private String semester;
    private Integer status;
}