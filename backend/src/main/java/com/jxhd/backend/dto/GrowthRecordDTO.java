package com.jxhd.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class GrowthRecordDTO {
    private Long studentId;
    private String content;
    private List<String> images;
}
