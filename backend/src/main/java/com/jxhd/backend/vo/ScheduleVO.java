package com.jxhd.backend.vo;

import lombok.Data;

@Data
public class ScheduleVO {
    private Long id;
    private Long classId;
    private String className;
    private Integer dayOfWeek;
    private Integer periodNo;
    private String subject;
    private Long teacherId;
    private String teacherName;
    private String startTime;
    private String endTime;
}
