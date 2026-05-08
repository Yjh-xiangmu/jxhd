-- 校园资讯模块新增表
-- 执行前请确认已选中 jxhd_db 数据库

-- 1. 校园资讯表（公告 / 新闻动态）
CREATE TABLE IF NOT EXISTS `campus_news` (
  `id`          bigint      NOT NULL AUTO_INCREMENT,
  `title`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `type`        varchar(20)  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'announcement' COMMENT 'announcement=公告  news=新闻动态',
  `content`     text         CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     COMMENT '正文内容',
  `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '1=已发布  0=草稿',
  `author_id`   bigint       NOT NULL COMMENT '发布人用户ID (sys_user.id)',
  `view_count`  int          NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='校园资讯（公告/新闻）';

-- 2. 亲子活动表
CREATE TABLE IF NOT EXISTS `campus_activity` (
  `id`               bigint      NOT NULL AUTO_INCREMENT,
  `title`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `description`      text         CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     COMMENT '活动描述',
  `location`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     COMMENT '活动地点',
  `activity_time`    datetime                                                           COMMENT '活动举办时间',
  `signup_deadline`  datetime                                                           COMMENT '报名截止时间',
  `max_count`        int                                                                COMMENT '最大报名人数 (NULL=不限)',
  `status`           tinyint      NOT NULL DEFAULT 1 COMMENT '1=报名中  2=已结束  0=草稿',
  `author_id`        bigint       NOT NULL COMMENT '发布人用户ID',
  `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='亲子活动';

-- 3. 活动报名表
CREATE TABLE IF NOT EXISTS `activity_signup` (
  `id`          bigint      NOT NULL AUTO_INCREMENT,
  `activity_id` bigint      NOT NULL COMMENT '活动ID',
  `parent_id`   bigint      NOT NULL COMMENT '报名家长用户ID',
  `student_id`  bigint      NOT NULL COMMENT '参加幼儿ID',
  `remark`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '备注',
  `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_student` (`activity_id`, `student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名';
