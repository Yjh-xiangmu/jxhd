package com.jxhd.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class AttendanceDTO {
    private Long studentId;
    private Long classId;
    private String date;
    private Integer status;
    private String absenceReason;
    private String contactMethod;
    private String contactNote;
    // 批量签到时使用
    private List<Long> studentIds;
}
