/**用户表**/
CREATE TABLE `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码(md5)',
  `lastLoginTime` timestamp NULL COMMENT '最近登录时间',
  `status` TINYINT(1) NULL DEFAULT 1,
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastUpdateTime` timestamp NUll DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `username` USING BTREE (`username` ASC))ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='用户表';

/**用户表**/
CREATE TABLE `topic` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(256) NOT NULL COMMENT '主题名',
    `creator` INT(11) NOT NULL COMMENT '创建者',
    `status` TINYINT(1) NULL DEFAULT 1,
    `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdateTime` timestamp NUll DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `name` USING BTREE (`name` ASC))ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='主题表';

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
    INDEX `topicName` USING BTREE (`topicName`(128) ASC))ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='主题表';