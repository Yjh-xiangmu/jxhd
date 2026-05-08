/*
 Navicat Premium Data Transfer

 Source Server         : yjh
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : jxhd_db

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 09/05/2026 02:55:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity_signup
-- ----------------------------
DROP TABLE IF EXISTS `activity_signup`;
CREATE TABLE `activity_signup`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `parent_id` bigint NOT NULL COMMENT '报名家长用户ID',
  `student_id` bigint NOT NULL COMMENT '参加幼儿ID',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_student`(`activity_id`, `student_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动报名' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_signup
-- ----------------------------
INSERT INTO `activity_signup` VALUES (1, 1, 4, 1, '', '2026-05-09 01:32:38');

-- ----------------------------
-- Table structure for attendance_record
-- ----------------------------
DROP TABLE IF EXISTS `attendance_record`;
CREATE TABLE `attendance_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '幼儿ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `date` date NOT NULL COMMENT '考勤日期',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=出勤  0=缺勤',
  `absence_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缺勤原因',
  `contact_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式: 电话/私信/未联系',
  `contact_note` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系说明',
  `recorder_id` bigint NULL DEFAULT NULL COMMENT '记录人（教师）用户ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_date`(`student_id`, `date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '幼儿考勤记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attendance_record
-- ----------------------------
INSERT INTO `attendance_record` VALUES (1, 1, 1, '2026-05-08', 1, NULL, NULL, NULL, 2, '2026-05-09 00:43:40', '2026-05-09 00:43:40');

-- ----------------------------
-- Table structure for campus_activity
-- ----------------------------
DROP TABLE IF EXISTS `campus_activity`;
CREATE TABLE `campus_activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '活动描述',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动地点',
  `activity_time` datetime(0) NULL DEFAULT NULL COMMENT '活动举办时间',
  `signup_deadline` datetime(0) NULL DEFAULT NULL COMMENT '报名截止时间',
  `max_count` int NULL DEFAULT NULL COMMENT '最大报名人数 (NULL=不限)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=报名中  2=已结束  0=草稿',
  `author_id` bigint NOT NULL COMMENT '发布人用户ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '亲子活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus_activity
-- ----------------------------
INSERT INTO `campus_activity` VALUES (1, '测试', '测试', '测试', '2026-05-09 01:00:00', '2026-05-10 02:00:00', NULL, 2, 1, '2026-05-09 00:21:46', '2026-05-09 00:21:46');

-- ----------------------------
-- Table structure for campus_news
-- ----------------------------
DROP TABLE IF EXISTS `campus_news`;
CREATE TABLE `campus_news`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'announcement' COMMENT 'announcement=公告  news=新闻动态',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '正文内容',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=已发布  0=草稿',
  `author_id` bigint NOT NULL COMMENT '发布人用户ID (sys_user.id)',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '校园资讯（公告/新闻）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus_news
-- ----------------------------
INSERT INTO `campus_news` VALUES (1, '测试', 'announcement', '测试', 1, 1, 0, '2026-05-09 00:20:47', '2026-05-09 00:20:47');

-- ----------------------------
-- Table structure for class_schedule
-- ----------------------------
DROP TABLE IF EXISTS `class_schedule`;
CREATE TABLE `class_schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `day_of_week` tinyint NOT NULL COMMENT '星期几: 1=周一 2=周二 3=周三 4=周四 5=周五',
  `period_no` tinyint NOT NULL COMMENT '第几节课 (1~8)',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '任课教师ID（可为空）',
  `start_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开始时间 HH:mm',
  `end_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结束时间 HH:mm',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_class_day_period`(`class_id`, `day_of_week`, `period_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级周课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class_schedule
-- ----------------------------
INSERT INTO `class_schedule` VALUES (1, 1, 1, 1, '艺术手工', 2, '10:00', '10:40', '2026-05-09 00:42:56', '2026-05-09 00:42:56');

-- ----------------------------
-- Table structure for forum_post
-- ----------------------------
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author_id` bigint NOT NULL COMMENT '发帖人ID',
  `author_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'admin/teacher/parent',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `images` json NULL COMMENT '附图列表',
  `is_pinned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `reply_count` int NOT NULL DEFAULT 0 COMMENT '回复数（冗余）',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_author`(`author_id`) USING BTREE,
  INDEX `idx_pinned`(`is_pinned`, `create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛帖子' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_post
-- ----------------------------
INSERT INTO `forum_post` VALUES (1, 4, 'parent', '孩子不吃饭', '孩子不吃饭', NULL, 1, 1, '2026-05-09 01:32:57');
INSERT INTO `forum_post` VALUES (2, 1, 'admin', '测试', '测试', NULL, 1, 2, '2026-05-09 01:33:27');
INSERT INTO `forum_post` VALUES (3, 2, 'teacher', '【活动总结】测试', '测试活动', '[\"/uploads/88383d426bb84eecb317faf9197f2395.png\"]', 0, 0, '2026-05-09 02:19:13');
INSERT INTO `forum_post` VALUES (5, 2, 'teacher', '测试多图', '多图', '[\"/uploads/bbdf786f36e945a4ba16354945377f67.png\", \"/uploads/449af46bd4c5483abd7fea47f25e7d90.png\", \"/uploads/7b5475114c684743b9ef24ac6a925264.png\"]', 0, 0, '2026-05-09 02:20:36');
INSERT INTO `forum_post` VALUES (6, 2, 'teacher', '测试日志', '测试', NULL, 0, 0, '2026-05-09 02:49:35');
INSERT INTO `forum_post` VALUES (8, 2, 'teacher', '测试日志', '测试', NULL, 0, 0, '2026-05-09 02:55:01');
INSERT INTO `forum_post` VALUES (9, 2, 'teacher', '【活动总结】测试', '测试日志', NULL, 0, 0, '2026-05-09 02:55:08');

-- ----------------------------
-- Table structure for forum_reply
-- ----------------------------
DROP TABLE IF EXISTS `forum_reply`;
CREATE TABLE `forum_reply`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '所属帖子ID',
  `author_id` bigint NOT NULL COMMENT '回复人ID',
  `author_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post`(`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛回复' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_reply
-- ----------------------------
INSERT INTO `forum_reply` VALUES (1, 1, 1, 'admin', 'ennn', '2026-05-09 01:33:17');
INSERT INTO `forum_reply` VALUES (2, 2, 2, 'teacher', '测试日志', '2026-05-09 02:50:21');
INSERT INTO `forum_reply` VALUES (3, 2, 2, 'teacher', '测试1', '2026-05-09 02:50:26');

-- ----------------------------
-- Table structure for growth_record
-- ----------------------------
DROP TABLE IF EXISTS `growth_record`;
CREATE TABLE `growth_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` bigint NOT NULL COMMENT '幼儿ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `author_id` bigint NOT NULL COMMENT '发布教师ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文字内容',
  `images` json NULL COMMENT '图片路径列表（JSON数组）',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student`(`student_id`) USING BTREE,
  INDEX `idx_class`(`class_id`) USING BTREE,
  INDEX `idx_author`(`author_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '成长动态' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of growth_record
-- ----------------------------
INSERT INTO `growth_record` VALUES (1, 1, 1, 2, '测试', '[\"/uploads/27b41a6b39c94cf4986d14baccad3e2d.png\"]', '2026-05-09 01:09:57');
INSERT INTO `growth_record` VALUES (2, 3, 1, 2, '测试', '[\"/uploads/9f766ebc72b743ab9fcf5129b3be479d.png\"]', '2026-05-09 02:15:21');

-- ----------------------------
-- Table structure for home_record
-- ----------------------------
DROP TABLE IF EXISTS `home_record`;
CREATE TABLE `home_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '幼儿ID',
  `parent_id` bigint NOT NULL COMMENT '提交家长ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '表现描述',
  `record_date` date NOT NULL COMMENT '记录日期',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student`(`student_id`) USING BTREE,
  INDEX `idx_parent`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '在家表现记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of home_record
-- ----------------------------
INSERT INTO `home_record` VALUES (1, 1, 4, '嘿嘿', '2026-05-08', '2026-05-09 01:32:31');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_id` bigint NOT NULL COMMENT '发送者ID',
  `to_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_from_to`(`from_id`, `to_id`) USING BTREE,
  INDEX `idx_to_read`(`to_id`, `is_read`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '私信消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 4, 2, '老师讲你好', 1, '2026-05-09 01:43:01');
INSERT INTO `message` VALUES (2, 2, 4, '家长你好', 1, '2026-05-09 01:43:26');

-- ----------------------------
-- Table structure for sys_class
-- ----------------------------
DROP TABLE IF EXISTS `sys_class`;
CREATE TABLE `sys_class`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称，如：大一班',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年级：大班/中班/小班',
  `head_teacher_id` bigint NULL DEFAULT NULL COMMENT '班主任用户ID（外键 sys_user.id）',
  `semester` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学期，如：2025-2026秋季',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_class
-- ----------------------------
INSERT INTO `sys_class` VALUES (1, '大一班', '大班', 2, '2024-2025春季', 1, '2026-05-08 18:30:09', '2026-05-08 18:30:09');
INSERT INTO `sys_class` VALUES (2, '大二班', '大班', 3, '2024-2025春季', 1, '2026-05-08 18:30:09', '2026-05-08 18:30:09');
INSERT INTO `sys_class` VALUES (3, '中一班', '中班', NULL, '2024-2025春季', 1, '2026-05-08 18:30:09', '2026-05-08 18:30:09');

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人账号（冗余，防止用户被删后查不到）',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作描述，如：创建班级',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模块，如：班级管理',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细内容（JSON）',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作IP',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------
INSERT INTO `sys_operation_log` VALUES (10, 2, 'teacher1', '发表回复', '论坛', '帖子：测试', '0:0:0:0:0:0:0:1', '2026-05-09 02:50:21');
INSERT INTO `sys_operation_log` VALUES (11, 2, 'teacher1', '删除帖子', '论坛', '标题：【活动总结】测试', '0:0:0:0:0:0:0:1', '2026-05-09 02:54:54');
INSERT INTO `sys_operation_log` VALUES (12, 2, 'teacher1', '发布帖子', '论坛', '标题：测试日志', '0:0:0:0:0:0:0:1', '2026-05-09 02:55:01');
INSERT INTO `sys_operation_log` VALUES (13, 2, 'teacher1', '发布帖子', '活动总结', '标题：【活动总结】测试', '0:0:0:0:0:0:0:1', '2026-05-09 02:55:08');

-- ----------------------------
-- Table structure for sys_parent_student
-- ----------------------------
DROP TABLE IF EXISTS `sys_parent_student`;
CREATE TABLE `sys_parent_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL COMMENT '家长用户ID',
  `student_id` bigint NOT NULL COMMENT '幼儿ID',
  `relation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关系：爸爸/妈妈/爷爷/奶奶等',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态：0待审核 1已通过 2已拒绝',
  `apply_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '申请时间',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `audit_by` bigint NULL DEFAULT NULL COMMENT '审核人（教师）用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_parent_student`(`parent_id`, `student_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家长幼儿绑定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_parent_student
-- ----------------------------
INSERT INTO `sys_parent_student` VALUES (1, 4, 1, '妈妈', 1, '2026-05-08 18:30:09', NULL, NULL);
INSERT INTO `sys_parent_student` VALUES (2, 5, 2, '爸爸', 1, '2026-05-08 18:30:09', NULL, NULL);
INSERT INTO `sys_parent_student` VALUES (3, 6, 3, '爸爸', 1, '2026-05-09 01:47:17', '2026-05-09 01:47:17', NULL);

-- ----------------------------
-- Table structure for sys_student
-- ----------------------------
DROP TABLE IF EXISTS `sys_student`;
CREATE TABLE `sys_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '幼儿姓名',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：1男 2女',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `class_id` bigint NULL DEFAULT NULL COMMENT '所在班级ID',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '幼儿头像',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1在园 0离园',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '幼儿表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_student
-- ----------------------------
INSERT INTO `sys_student` VALUES (1, '张小明', 1, '2020-03-15', 1, NULL, NULL, 1, '2026-05-08 18:30:09', '2026-05-08 18:30:09');
INSERT INTO `sys_student` VALUES (2, '李艺彤', 2, '2020-07-22', 2, NULL, NULL, 1, '2026-05-08 18:30:09', '2026-05-08 18:30:09');
INSERT INTO `sys_student` VALUES (3, '王浩宇', 1, '2019-11-05', 1, NULL, NULL, 1, '2026-05-08 18:30:09', '2026-05-08 18:30:09');
INSERT INTO `sys_student` VALUES (4, '小小', 1, '2024-01-08', NULL, NULL, '', 1, '2026-05-08 20:07:42', '2026-05-08 20:07:42');

-- ----------------------------
-- Table structure for sys_teacher_class
-- ----------------------------
DROP TABLE IF EXISTS `sys_teacher_class`;
CREATE TABLE `sys_teacher_class`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint NOT NULL COMMENT '教师用户ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `is_head` tinyint NOT NULL DEFAULT 0 COMMENT '是否班主任：1是 0否',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_teacher_class`(`teacher_id`, `class_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师班级关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_teacher_class
-- ----------------------------
INSERT INTO `sys_teacher_class` VALUES (1, 2, 1, 1, '2026-05-08 18:30:09');
INSERT INTO `sys_teacher_class` VALUES (2, 3, 2, 1, '2026-05-08 18:30:09');
INSERT INTO `sys_teacher_class` VALUES (3, 2, 2, 0, '2026-05-08 18:30:09');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（明文，后续改为BCrypt）',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：admin/teacher/parent',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', 'admin', '系统管理员', '13800000001', NULL, 1, '2026-05-08 18:30:08', '2026-05-08 18:30:08');
INSERT INTO `sys_user` VALUES (2, 'teacher1', '123456', 'teacher', '李老师', '13800000002', NULL, 1, '2026-05-08 18:30:08', '2026-05-08 18:30:08');
INSERT INTO `sys_user` VALUES (3, 'teacher2', '123456', 'teacher', '王老师', '13800000003', NULL, 1, '2026-05-08 18:30:08', '2026-05-08 18:30:08');
INSERT INTO `sys_user` VALUES (4, 'parent1', '123456', 'parent', '张小明妈妈', '13800000004', NULL, 1, '2026-05-08 18:30:08', '2026-05-08 18:30:08');
INSERT INTO `sys_user` VALUES (5, 'parent2', '123456', 'parent', '李艺彤爸爸', '13800000005', NULL, 1, '2026-05-08 18:30:08', '2026-05-08 18:30:08');
INSERT INTO `sys_user` VALUES (6, 'parent3', '123456', 'parent', '王浩宇爸爸', '111111', NULL, 1, '2026-05-09 01:46:58', '2026-05-09 01:46:58');
INSERT INTO `sys_user` VALUES (7, 'parent4', '123456', 'parent', '测试', '122222', NULL, 1, '2026-05-09 02:33:56', '2026-05-09 02:33:56');

SET FOREIGN_KEY_CHECKS = 1;
