/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : 192.168.1.119:3306
 Source Schema         : mist_cloud

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 27/08/2023 15:30:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '文件名',
  `size` bigint unsigned NOT NULL DEFAULT '0' COMMENT '文件大小',
  `type` varchar(255) NOT NULL DEFAULT 'binary' COMMENT '文件类型',
  `folder_id` bigint NOT NULL COMMENT '所在文件夹 id',
  `origin_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `duplicate_times` int unsigned NOT NULL DEFAULT '0' COMMENT '重名次数',
  `md5` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP,
  `deleted` int NOT NULL DEFAULT '0',
  `deleted_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1695358856554745857 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Table structure for sys_folder
-- ----------------------------
DROP TABLE IF EXISTS `sys_folder`;
CREATE TABLE `sys_folder` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '文件名',
  `folder_size` bigint NOT NULL DEFAULT '0' COMMENT '文件大小',
  `parent_id` bigint unsigned NOT NULL COMMENT '父文件夹 id',
  `user_id` bigint unsigned NOT NULL COMMENT '文件所有者 id',
  `modify_time` datetime NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT '0',
  `deleted_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1695321694929031170 DEFAULT CHARSET=utf8mb3;

INSERT INTO sys_folder (id, name, folder_size, parent_id, user_id, modify_time, create_time, deleted, deleted_time) VALUES (1, '全部文件', 0, 0, 1, '2023-08-28 11:34:02', '2023-08-28 11:34:02', 0, null);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `total_capacity` bigint unsigned NOT NULL DEFAULT '0' COMMENT '总容量',
  `root_folder_id` bigint NOT NULL COMMENT '用户根目录文件夹 id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

INSERT INTO sys_user (id, username, password, total_capacity, root_folder_id) VALUES (1, 'admin', 'admin', 0, 1);

-- ----------------------------
-- Table structure for uoload_chunk
-- ----------------------------
DROP TABLE IF EXISTS `uoload_chunk`;
CREATE TABLE `uoload_chunk` (
  `id` bigint NOT NULL,
  `chunk_number` int NOT NULL DEFAULT '0' COMMENT '当前的文件块，从 1 开始',
  `total_chunks` int NOT NULL DEFAULT '0' COMMENT '总块数',
  `chunk_size` bigint NOT NULL DEFAULT '0' COMMENT '理论分片大小',
  `total_size` bigint NOT NULL DEFAULT '0' COMMENT '文件总大小',
  `current_chunk_size` bigint NOT NULL DEFAULT '0' COMMENT '当前分片实际大小',
  `identifier` varchar(1024) NOT NULL DEFAULT 'default' COMMENT '唯一标识',
  `file_name` varchar(1024) NOT NULL DEFAULT 'default' COMMENT '文件名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件上传分片';

-- ----------------------------
-- Function structure for get_child_files
-- ----------------------------
DROP FUNCTION IF EXISTS `get_child_files`;
delimiter ;;
CREATE FUNCTION `get_child_files`(in_id bigint)
 RETURNS varchar(10000) CHARSET utf8mb4
begin
    DECLARE ids varchar(10000);

    SELECT GROUP_CONCAT(id) INTO ids FROM sys_file WHERE folder_id = in_id AND deleted = 0;

return ids;
end
;;
delimiter ;

-- ----------------------------
-- Function structure for get_child_folders
-- ----------------------------
DROP FUNCTION IF EXISTS `get_child_folders`;
delimiter ;;
CREATE FUNCTION `get_child_folders`(in_id bigint)
 RETURNS varchar(10000) CHARSET utf8mb4
begin
    DECLARE ids varchar(10000);

    SELECT GROUP_CONCAT(id) INTO ids FROM sys_folder WHERE parent_id = in_id AND deleted = 0;

return ids;
end
;;
delimiter ;

-- ----------------------------
-- Function structure for get_chlid_lists
-- ----------------------------
DROP FUNCTION IF EXISTS `get_chlid_lists`;
delimiter ;;
CREATE FUNCTION `get_chlid_lists`(in_id bigint)
 RETURNS varchar(10000) CHARSET utf8mb4
  DETERMINISTIC
BEGIN
    DECLARE ids VARCHAR(10000) DEFAULT '';
    DECLARE temp_ids VARCHAR(10000);

    SET temp_ids = in_id;
    WHILE temp_ids IS NOT NULL
        DO
            SET ids = CONCAT_WS(',', ids, temp_ids);
SELECT GROUP_CONCAT(id) INTO temp_ids FROM sys_folder WHERE FIND_IN_SET(parent_id, temp_ids) > 0 ORDER BY parent_id DESC ;
end WHILE;
RETURN ids;

END
;;
delimiter ;

-- ----------------------------
-- Function structure for get_parent_list
-- ----------------------------
DROP FUNCTION IF EXISTS `get_parent_list`;
delimiter ;;
CREATE FUNCTION `get_parent_list`(in_id bigint)
 RETURNS varchar(10000) CHARSET utf8mb4
begin
    declare ids varchar(10000);
    declare tempid bigint;

    set tempid = in_id;
    while tempid != 0 do
        set ids = CONCAT_WS(',',ids,tempid);
        select parent_id into tempid from sys_folder where id=tempid;
end while;
return ids;
end
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
