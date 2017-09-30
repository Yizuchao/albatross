package com.yogi.albatross.constants.common;

public enum PublishQos {
    ZERO(0),ONE(1),TWO(2);

    private int code;

    PublishQos(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PublishQos valueOf(int code){
        PublishQos[] values=PublishQos.values();
        int size=values.length;
        for (int i = 0; i < size; i++) {
            if(values[i].code==code){
                return values[i];
            }
        }
        return null;
    }
}
