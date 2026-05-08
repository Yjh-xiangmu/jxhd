-- 课表与考勤模块新增表
-- 执行前请确认已选中 jxhd_db 数据库

-- 1. 班级周课表（管理员/园长维护）
CREATE TABLE IF NOT EXISTS `class_schedule` (
  `id`          bigint      NOT NULL AUTO_INCREMENT,
  `class_id`    bigint      NOT NULL COMMENT '班级ID',
  `day_of_week` tinyint     NOT NULL COMMENT '星期几: 1=周一 2=周二 3=周三 4=周四 5=周五',
  `period_no`   tinyint     NOT NULL COMMENT '第几节课 (1~8)',
  `subject`     varchar(50)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `teacher_id`  bigint      NULL DEFAULT NULL COMMENT '任课教师ID（可为空）',
  `start_time`  varchar(10)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开始时间 HH:mm',
  `end_time`    varchar(10)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结束时间 HH:mm',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_day_period` (`class_id`, `day_of_week`, `period_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级周课表';

-- 2. 幼儿每日考勤记录
CREATE TABLE IF NOT EXISTS `attendance_record` (
  `id`              bigint      NOT NULL AUTO_INCREMENT,
  `student_id`      bigint      NOT NULL COMMENT '幼儿ID',
  `class_id`        bigint      NOT NULL COMMENT '班级ID',
  `date`            date        NOT NULL COMMENT '考勤日期',
  `status`          tinyint     NOT NULL DEFAULT 1 COMMENT '1=出勤  0=缺勤',
  `absence_reason`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缺勤原因',
  `contact_method`  varchar(20)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式: 电话/私信/未联系',
  `contact_note`    varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系说明',
  `recorder_id`     bigint      NULL DEFAULT NULL COMMENT '记录人（教师）用户ID',
  `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_date` (`student_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='幼儿考勤记录';
