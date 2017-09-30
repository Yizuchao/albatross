package com.yogi.albatross.constants.head;

/**
 * 控制报文头部类型
 */
public enum FixedHeadType {
    CONNECT("connect"),PUBLISH("publish"),PUBREL("pubrel"),SUBSCRIBE("subscribe");
    private String desc;

    FixedHeadType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static FixedHeadType valueOf(byte code){
        if(code==(byte)0x10){
            return CONNECT;
        }
        if(code>=0x30 && code<=0x3f){
            return PUBLISH;
        }
        if(code==(byte)0xc2){
            return PUBREL;
        }
        if(code==(byte)0x82){
            return SUBSCRIBE;
        }
        return null;
    }
}
