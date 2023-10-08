/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : 192.168.1.119:3306
 Source Schema         : mist_cloud_dev

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 08/10/2023 10:35:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for share_link
-- ----------------------------
DROP TABLE IF EXISTS `share_link`;
CREATE TABLE `share_link` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `unique_key` varchar(20) NOT NULL COMMENT '唯一标识',
  `user_id` bigint NOT NULL COMMENT '分享的用户',
  `file_id` bigint NOT NULL COMMENT '对应的文件id',
  `code` char(4) NOT NULL COMMENT '提取码',
  `visit_limit` int DEFAULT '0' COMMENT '访问人数限制',
  `visit_times` int DEFAULT '0' COMMENT '访问次数',
  `download_times` int NOT NULL DEFAULT '0' COMMENT '下载次数',
  `description` varchar(50) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb3 COMMENT='文件分享链接';

-- ----------------------------
-- Records of share_link
-- ----------------------------
BEGIN;
COMMIT;

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
  `relative_path` varchar(300) NOT NULL COMMENT '真实路径',
  `origin_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `md5` varchar(255) DEFAULT NULL,
  `duplicate_times` int unsigned NOT NULL DEFAULT '0' COMMENT '重名次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` int NOT NULL DEFAULT '0',
  `deleted_time` datetime DEFAULT NULL,
  `user_id` bigint NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1710828233634746369 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_folder
-- ----------------------------
DROP TABLE IF EXISTS `sys_folder`;
CREATE TABLE `sys_folder` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '文件名',
  `parent_id` bigint unsigned NOT NULL COMMENT '父文件夹 id',
  `user_id` bigint unsigned NOT NULL COMMENT '文件所有者 id',
  `modify_time` datetime NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` int NOT NULL DEFAULT '0',
  `deleted_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1710826310571528193 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of sys_folder
-- ----------------------------
BEGIN;
INSERT INTO `sys_folder` (`id`, `name`, `parent_id`, `user_id`, `modify_time`, `create_time`, `deleted`, `deleted_time`) VALUES (1699391866123980800, '全部文件', 0, 3, '2023-09-17 10:51:33', '2023-09-17 10:51:33', 0, NULL);
INSERT INTO `sys_folder` (`id`, `name`, `parent_id`, `user_id`, `modify_time`, `create_time`, `deleted`, `deleted_time`) VALUES (1699391866123980801, '全部文件', 0, 4, '2023-10-08 10:12:17', '2023-10-08 10:12:17', 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(30) NOT NULL COMMENT '邮箱',
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `total_capacity` bigint unsigned NOT NULL DEFAULT '0' COMMENT '总容量',
  `root_folder_id` bigint NOT NULL COMMENT '用户根目录文件夹 id',
  `used_capacity` bigint unsigned NOT NULL DEFAULT '0' COMMENT '已使用容量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `email`, `username`, `password`, `total_capacity`, `root_folder_id`, `used_capacity`) VALUES (3, 'gongjiatian00@qq.com', 'admin', 'admin', 21474836480, 1699391866123980800, 0);
INSERT INTO `sys_user` (`id`, `email`, `username`, `password`, `total_capacity`, `root_folder_id`, `used_capacity`) VALUES (4, '3062681810@qq.com', 'tom123', '12345', 0, 1699391866123980801, 0);
COMMIT;

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
 RETURNS varchar(10000) CHARSET utf8mb3
  DETERMINISTIC
BEGIN
    DECLARE ids VARCHAR(10000) DEFAULT '';
    DECLARE temp_ids VARCHAR(10000);

    SET temp_ids = in_id;
    WHILE temp_ids IS NOT NULL
        DO
            SET ids = CONCAT_WS(',', ids, temp_ids);
            SELECT GROUP_CONCAT(id)
            INTO temp_ids
            FROM sys_folder
            WHERE FIND_IN_SET(parent_id, temp_ids) > 0
            ORDER BY parent_id DESC;
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
 RETURNS varchar(10000) CHARSET utf8mb3
begin
    DECLARE ids varchar(10000);
    DECLARE tempid bigint;

    SET tempid = in_id;

    WHILE tempid != 0
        DO
            set ids = CONCAT_WS(',', ids, tempid);
            select parent_id INTO tempid FROM sys_folder WHERE id = tempid;
        END WHILE;
    RETURN ids;
end
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
