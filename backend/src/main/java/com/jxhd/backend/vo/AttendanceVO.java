package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AttendanceVO {
    // 学生基本信息
    private Long studentId;
    private String studentName;
    private Integer gender;
    private LocalDate birthday;
    private String remark;

    // 考勤信息（null 表示当天未记录）
    private Long attendanceId;
    private Integer status;       // 1=出勤 0=缺勤 null=未记录
    private String absenceReason;
    private String contactMethod;
    private String contactNote;
}
