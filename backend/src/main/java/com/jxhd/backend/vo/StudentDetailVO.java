package com.jxhd.backend.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentDetailVO {

    private Long id;
    private String name;
    private Integer gender;
    private String genderLabel;
    private LocalDate birthday;
    private Long classId;
    private String className;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;

    /** 绑定的家长列表 */
    private List<ParentInfo> parents;

    @Data
    public static class ParentInfo {
        private Long bindId;       // sys_parent_student.id
        private Long parentId;     // 家长用户ID
        private String realName;   // 家长姓名
        private String username;   // 登录账号
        private String phone;      // 手机号
        private String relation;   // 关系（爸爸/妈妈等）
        private Integer status;    // 绑定审核状态 0待审核 1已通过 2已拒绝
        private String statusLabel;
    }
}