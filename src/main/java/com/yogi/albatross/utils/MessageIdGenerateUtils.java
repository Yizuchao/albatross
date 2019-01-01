package com.yogi.albatross.utils;

import java.util.UUID;

/**
 * 消息id生成器，暂时先使用uuid算法（单台机器每秒千万不重复）。
 */
public class MessageIdGenerateUtils {
    public static String messageId(){
        return UUID.randomUUID().toString();
    }
}
