package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GrowthRecordVO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;
    private Long authorId;
    private String authorName;
    private String content;
    private List<String> images;
    private LocalDateTime createTime;
}
