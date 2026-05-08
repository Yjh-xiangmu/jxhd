package com.jxhd.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_parent_student")
public class ParentStudent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private Long studentId;
    private String relation;
    /** 0待审核 1已通过 2已拒绝 */
    private Integer status;
    private LocalDateTime applyTime;
    private LocalDateTime auditTime;
    private Long auditBy;
}
