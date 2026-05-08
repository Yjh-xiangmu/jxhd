package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("campus_activity")
public class CampusActivity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime activityTime;
    private LocalDateTime signupDeadline;
    private Integer maxCount;
    private Integer status;
    private Long authorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
