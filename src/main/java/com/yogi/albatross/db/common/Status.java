package com.yogi.albatross.db.common;

public enum Status {
    OK(0,"ok/是/true"),NO(1,"no/否/false");
    private Integer code;
    private String msg;

    Status(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public static Status valueOf(Integer code){
        Status[] statuses=Status.values();
        for (Status status:statuses){
            if(status.code.equals(code)){
                return status;
            }
        }
        return null;
    }
}
