package com.yogi.albatross.constants.head;

/**
 * 控制报文头部类型
 */
public enum FixedHeadType {
    CONNECT((byte)0x10,"connect");
    private byte code;
    private String desc;

    FixedHeadType(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public byte getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }

    public static FixedHeadType valueOf(byte code){
        FixedHeadType[] values=FixedHeadType.values();
        for (int i=0;i<values.length;i++) {
            if(values[i].code==code){
                return values[i];
            }
        }
        return null;
    }
}
