-- 在家表现记录（家长填写）
CREATE TABLE IF NOT EXISTS `home_record` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `student_id`  BIGINT       NOT NULL COMMENT '幼儿ID',
  `parent_id`   BIGINT       NOT NULL COMMENT '提交家长ID',
  `content`     TEXT         NOT NULL COMMENT '表现描述',
  `record_date` DATE         NOT NULL COMMENT '记录日期',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_parent`  (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在家表现记录';

-- 论坛帖子
CREATE TABLE IF NOT EXISTS `forum_post` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `author_id`   BIGINT       NOT NULL COMMENT '发帖人ID',
  `author_role` VARCHAR(20)  NOT NULL COMMENT 'admin/teacher/parent',
  `title`       VARCHAR(200) NOT NULL COMMENT '帖子标题',
  `content`     TEXT         NOT NULL COMMENT '帖子内容',
  `is_pinned`   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `reply_count` INT          NOT NULL DEFAULT 0 COMMENT '回复数（冗余）',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_author`  (`author_id`),
  KEY `idx_pinned`  (`is_pinned`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子';

-- 论坛回复
CREATE TABLE IF NOT EXISTS `forum_reply` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `post_id`     BIGINT       NOT NULL COMMENT '所属帖子ID',
  `author_id`   BIGINT       NOT NULL COMMENT '回复人ID',
  `author_role` VARCHAR(20)  NOT NULL,
  `content`     TEXT         NOT NULL,
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛回复';
