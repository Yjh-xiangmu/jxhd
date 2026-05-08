package com.jxhd.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentDTO {
    private Long id;
    private String name;
    private Integer gender;
    private LocalDate birthday;
    private Long classId;
    private String avatar;
    private String remark;
    private Integer status;
}