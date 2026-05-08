package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("attendance_record")
public class AttendanceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long classId;
    private LocalDate date;
    private Integer status;
    private String absenceReason;
    private String contactMethod;
    private String contactNote;
    private Long recorderId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
