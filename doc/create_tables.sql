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
    `creator` char(23) NOT NULL COMMENT '创建者',
    `status` TINYINT(1) NULL DEFAULT 1,
    `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdateTime` timestamp NUll DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name`(`name` ASC) USING BTREE)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='主题表';

/**订阅表**/
CREATE TABLE `subscribe` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `topicName` VARCHAR(256) NOT NULL COMMENT '主题主键',
    `subscriber` char(23) NOT NULL COMMENT '订阅者',
    `qos`    TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'qos等级',
    `status` TINYINT(1) NULL DEFAULT 1,
    `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdateTime` timestamp NUll DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `topicName` USING BTREE (`topicName`(128) ASC))ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订阅表';

/**会话表**/
CREATE TABLE `session` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clientId` char(23) NOT NULL,
  `serverSession` text  NULL COMMENT '服务端会话',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastUpdateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '记录是否有效。1-有效，0-无效',
  PRIMARY KEY (`id`),
  KEY `IDX_CLIENTID` (`clientId`, `status` ) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户的session表';

/**消息表*/
CREATE TABLE `message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `content` VARBINARY(1000) NOT NULL COMMENT ' 消息内容',
  `sended` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '是否发送成功。1-发送成功，0-失败',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `lastUpdateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
  `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '记录是否有效。1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `CREATE_TIME_IDX` USING BTREE (`create_time`) VISIBLE)
ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COMMENT = '消息表';

CREATE TABLE `client_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `messageId` BIGINT NOT NULL COMMENT '消息id',
  `retry` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '已经重试的次数',
  `clientId` VARCHAR(50) NOT NULL COMMENT '客户端id',
  `createTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastUpdateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` TINYINT(2) NULL DEFAULT 1 COMMENT '记录有效性。0-无效，1-有效',
  PRIMARY KEY (`id`),
  INDEX `CLIENT_ID_RETRY_STATUS_IDX` (`clientId` ASC, `retry` ASC, `status` ASC) VISIBLE,
  UNIQUE INDEX `CLIENT_ID_MESSAGE_ID_IDX` (`clientId`, `messageId`) VISIBLE USING BTREE
)ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COMMENT = '客户端消息表';