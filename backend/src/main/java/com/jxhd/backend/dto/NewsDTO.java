package com.jxhd.backend.dto;

import lombok.Data;

@Data
public class NewsDTO {
    private Long id;
    private String title;
    private String type;
    private String content;
    private Integer status;
}
