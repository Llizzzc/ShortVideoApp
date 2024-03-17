# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20051
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: localhost (MySQL 11.1.2-MariaDB)
# 数据库: tiktok
# 生成时间: 2023-09-18 09:04:45 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# 转储表 comment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` varchar(32) NOT NULL,
  `vloger_id` varchar(32) NOT NULL COMMENT '博主id',
  `father_comment_id` varchar(32) NOT NULL COMMENT '父评论id',
  `vlog_id` varchar(32) NOT NULL COMMENT '视频id',
  `comment_user_id` varchar(32) NOT NULL COMMENT '评论用户id',
  `content` varchar(64) NOT NULL COMMENT '留言内容',
  `like_counts` int(8) NOT NULL COMMENT '留言的点赞总数',
  `create_time` datetime NOT NULL COMMENT '留言时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评论表';



# 转储表 fans
# ------------------------------------------------------------

DROP TABLE IF EXISTS `fans`;

CREATE TABLE `fans` (
  `id` varchar(32) NOT NULL,
  `vloger_id` varchar(32) NOT NULL COMMENT '当前用户id',
  `fan_id` varchar(32) NOT NULL COMMENT '粉丝id',
  `is_fan_friend_of_mine` int(1) NOT NULL COMMENT '粉丝是否是当前用户的关注, 如果是, 则双方此字段都需要设置为1, 否则设置为0',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='粉丝表\n\n';



# 转储表 my_liked_vlog
# ------------------------------------------------------------

DROP TABLE IF EXISTS `my_liked_vlog`;

CREATE TABLE `my_liked_vlog` (
  `id` varchar(32) NOT NULL,
  `user_id` varchar(32) NOT NULL COMMENT '用户id',
  `vlog_id` varchar(32) NOT NULL COMMENT '视频id',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='点赞短视频关联表\n';



# 转储表 users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` varchar(32) NOT NULL,
  `mobile` varchar(16) NOT NULL COMMENT '手机号',
  `nickname` varchar(16) NOT NULL COMMENT '昵称',
  `tiktok_num` varchar(16) NOT NULL COMMENT '视频号',
  `face` varchar(128) NOT NULL COMMENT '头像',
  `sex` int(4) NOT NULL COMMENT '性别 1:男  0:女  2:保密',
  `birthday` date NOT NULL COMMENT '生日',
  `country` varchar(32) NOT NULL COMMENT '国家',
  `province` varchar(32) NOT NULL COMMENT '省份',
  `city` varchar(32) NOT NULL COMMENT '城市',
  `district` varchar(32) NOT NULL COMMENT '区县',
  `description` varchar(32) NOT NULL COMMENT '简介',
  `bg_img` varchar(255) NOT NULL COMMENT '个人介绍的背景图',
  `can_tiktok_num_be_updated` int(1) NOT NULL COMMENT '视频号能否被修改 1：可以修改；0：无法修改',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  `password` varchar(64) NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `mobile` (`mobile`) USING BTREE,
  UNIQUE KEY `tiktok_num` (`tiktok_num`),
  UNIQUE KEY `nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';



# 转储表 vlog
# ------------------------------------------------------------

DROP TABLE IF EXISTS `vlog`;

CREATE TABLE `vlog` (
  `id` varchar(32) NOT NULL,
  `vloger_id` varchar(32) NOT NULL COMMENT '对应用户表id',
  `url` varchar(255) NOT NULL COMMENT '视频播放地址',
  `cover` varchar(356) NOT NULL COMMENT '视频封面',
  `title` varchar(32) NOT NULL COMMENT '视频标题',
  `width` int(4) NOT NULL COMMENT '视频width',
  `height` int(4) NOT NULL COMMENT '视频height',
  `like_counts` int(8) NOT NULL COMMENT '点赞总数',
  `comments_counts` int(8) NOT NULL COMMENT '评论总数',
  `is_private` int(1) NOT NULL COMMENT '是否私密',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='短视频表';




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
