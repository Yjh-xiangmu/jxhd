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

 Date: 09/05/2026 00:04:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;
