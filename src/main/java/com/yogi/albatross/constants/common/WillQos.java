package com.yogi.albatross.constants.common;

public enum WillQos {
    ZERO(0),ONE(1),TWO(2);
    private int code;

    WillQos(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static WillQos valueOf(int code){
        WillQos[] values=WillQos.values();
        int size=values.length;
        for (int i = 0; i < size; i++) {
            if(values[i].code==code){
                return values[i];
            }
        }
        return null;
    }
}
