package com.yogi.albatross.constants.common;

public class Constants {
    public static final String PTOTOCOL_NAME="MQTT|MQIsdp";
    public static final int PROTOCOL_LEVEL=4;//MQTT 3.1.1
    public static final int RETRY_COUNT=3;//消息重试次数
    public static final int RETRY_BASE=30;//消息重试的间隔基数
    public static final boolean ANONYMOUSE_SUPPORT=false;//默认不支持匿名连接
}
