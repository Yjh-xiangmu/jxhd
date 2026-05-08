package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("class_schedule")
public class ClassSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classId;
    private Integer dayOfWeek;
    private Integer periodNo;
    private String subject;
    private Long teacherId;
    private String startTime;
    private String endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
