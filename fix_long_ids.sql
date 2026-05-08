-- 可选：仅在当前库已经产生过雪花长 ID 时执行一次。
-- 作用：把用户、班级、幼儿等主键重新整理为 1,2,3...，并同步修复关联字段。
-- 建议先备份数据库，再在 jxhd_db 库中执行。

USE `jxhd_db`;

SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;

CREATE TEMPORARY TABLE tmp_user_id_map AS
SELECT id AS old_id, ROW_NUMBER() OVER (ORDER BY id) AS new_id
FROM sys_user;

CREATE TEMPORARY TABLE tmp_class_id_map AS
SELECT id AS old_id, ROW_NUMBER() OVER (ORDER BY id) AS new_id
FROM sys_class;

CREATE TEMPORARY TABLE tmp_student_id_map AS
SELECT id AS old_id, ROW_NUMBER() OVER (ORDER BY id) AS new_id
FROM sys_student;

CREATE TEMPORARY TABLE tmp_parent_student_id_map AS
SELECT id AS old_id, ROW_NUMBER() OVER (ORDER BY id) AS new_id
FROM sys_parent_student;

CREATE TEMPORARY TABLE tmp_operation_log_id_map AS
SELECT id AS old_id, ROW_NUMBER() OVER (ORDER BY id) AS new_id
FROM sys_operation_log;

UPDATE sys_class c
JOIN tmp_user_id_map m ON c.head_teacher_id = m.old_id
SET c.head_teacher_id = m.new_id;

UPDATE sys_teacher_class tc
JOIN tmp_user_id_map m ON tc.teacher_id = m.old_id
SET tc.teacher_id = m.new_id;

UPDATE sys_teacher_class tc
JOIN tmp_class_id_map m ON tc.class_id = m.old_id
SET tc.class_id = m.new_id;

UPDATE sys_student s
JOIN tmp_class_id_map m ON s.class_id = m.old_id
SET s.class_id = m.new_id;

UPDATE sys_parent_student ps
JOIN tmp_user_id_map m ON ps.parent_id = m.old_id
SET ps.parent_id = m.new_id;

UPDATE sys_parent_student ps
JOIN tmp_student_id_map m ON ps.student_id = m.old_id
SET ps.student_id = m.new_id;

UPDATE sys_parent_student ps
JOIN tmp_user_id_map m ON ps.audit_by = m.old_id
SET ps.audit_by = m.new_id;

UPDATE sys_operation_log l
JOIN tmp_user_id_map m ON l.user_id = m.old_id
SET l.user_id = m.new_id;

UPDATE sys_user u
JOIN tmp_user_id_map m ON u.id = m.old_id
SET u.id = m.new_id;

UPDATE sys_class c
JOIN tmp_class_id_map m ON c.id = m.old_id
SET c.id = m.new_id;

UPDATE sys_student s
JOIN tmp_student_id_map m ON s.id = m.old_id
SET s.id = m.new_id;

UPDATE sys_parent_student ps
JOIN tmp_parent_student_id_map m ON ps.id = m.old_id
SET ps.id = m.new_id;

UPDATE sys_operation_log l
JOIN tmp_operation_log_id_map m ON l.id = m.old_id
SET l.id = m.new_id;

SELECT COALESCE(MAX(id), 0) + 1 INTO @next_id FROM sys_user;
SET @sql = CONCAT('ALTER TABLE sys_user AUTO_INCREMENT = ', @next_id);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT COALESCE(MAX(id), 0) + 1 INTO @next_id FROM sys_class;
SET @sql = CONCAT('ALTER TABLE sys_class AUTO_INCREMENT = ', @next_id);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT COALESCE(MAX(id), 0) + 1 INTO @next_id FROM sys_student;
SET @sql = CONCAT('ALTER TABLE sys_student AUTO_INCREMENT = ', @next_id);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT COALESCE(MAX(id), 0) + 1 INTO @next_id FROM sys_parent_student;
SET @sql = CONCAT('ALTER TABLE sys_parent_student AUTO_INCREMENT = ', @next_id);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT COALESCE(MAX(id), 0) + 1 INTO @next_id FROM sys_teacher_class;
SET @sql = CONCAT('ALTER TABLE sys_teacher_class AUTO_INCREMENT = ', @next_id);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT COALESCE(MAX(id), 0) + 1 INTO @next_id FROM sys_operation_log;
SET @sql = CONCAT('ALTER TABLE sys_operation_log AUTO_INCREMENT = ', @next_id);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;
