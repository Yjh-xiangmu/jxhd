-- 成长动态表
CREATE TABLE IF NOT EXISTS `growth_record` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id`  BIGINT       NOT NULL COMMENT '幼儿ID',
  `class_id`    BIGINT       NOT NULL COMMENT '班级ID',
  `author_id`   BIGINT       NOT NULL COMMENT '发布教师ID',
  `content`     TEXT         NOT NULL COMMENT '文字内容',
  `images`      JSON                  COMMENT '图片路径列表（JSON数组）',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_class`   (`class_id`),
  KEY `idx_author`  (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成长动态';
