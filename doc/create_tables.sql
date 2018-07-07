CREATE SCHEMA `albatross` DEFAULT CHARACTER SET utf8mb4 ;/*创建数据库*/
/**用户表**/
CREATE TABLE `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` CHAR (128) NOT NULL COMMENT '密码(md5)',
  `lastLoginTime` timestamp NULL COMMENT '最近登录时间',
  `session` TEXT NULL COMMENT "会话",
  `status` TINYINT(1) NULL DEFAULT 1,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastUpdateTime` timestamp NUll DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `username` USING BTREE (`username` ASC))ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

/**主题表**/
CREATE TABLE `topic` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(256) NOT NULL COMMENT '主题名',
    `creator` INT(11) NOT NULL COMMENT '创建者',
    `status` TINYINT(1) NULL DEFAULT 1,
    `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdateTime` timestamp NUll DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `name` USING BTREE (`name` ASC))ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='主题表';

/**订阅表**/
CREATE TABLE `subscribe` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `topicName` VARCHAR(256) NOT NULL COMMENT '主题主键',
    `subscriber` INT(11) NOT NULL COMMENT '订阅者',
    `qos`    TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'qos等级',
    `status` TINYINT(1) NULL DEFAULT 1,
    `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdateTime` timestamp NUll DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `topicName` USING BTREE (`topicName`(128) ASC))ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订阅表';

CREATE TABLE `user_session` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `serverSession` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `willTopic` varchar(500) DEFAULT NULL,
  `willMessage` varchar(2000) DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `lastUpdateTime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_USERID` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户的session表'