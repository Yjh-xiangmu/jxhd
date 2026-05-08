-- 私信表
CREATE TABLE IF NOT EXISTS `message` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `from_id`     BIGINT       NOT NULL COMMENT '发送者ID',
  `to_id`       BIGINT       NOT NULL COMMENT '接收者ID',
  `content`     TEXT         NOT NULL COMMENT '消息内容',
  `is_read`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已读',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_from_to` (`from_id`, `to_id`),
  KEY `idx_to_read` (`to_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信消息';
