package com.jxhd.backend.dto;

import lombok.Data;

@Data
public class ScheduleDTO {
    private Long id;
    private Long classId;
    private Integer dayOfWeek;
    private Integer periodNo;
    private String subject;
    private Long teacherId;
    private String startTime;
    private String endTime;
}
