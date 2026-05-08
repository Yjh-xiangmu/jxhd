package com.jxhd.backend.dto;

import lombok.Data;

@Data
public class ParentBindDTO {
    private Long studentId;
    private Long parentId;
    private String relation; // 爸爸/妈妈/爷爷/奶奶等
}