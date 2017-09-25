package com.yogi.albatross.constants.ack;

public enum ConnAck implements IAck{
    OK((byte)0x00,"ok"),
    PROTOL_NOT_SUPPORT((byte)0x01,"协议版本不支持"),
    ERROR_CLIENT_ID((byte)0x02,"不合格的客户端标识符"),
    SERVER_ERROR((byte)0x03,"服务端不可用"),
    ERROR_USERNAME_OR_PSW((byte)0x04,"无效的用户名或密码"),
    NOT_AUTH((byte)0x05,"客户端未被授权连接到此服务器")
    ;
    private byte code;
    private String desc;

    ConnAck(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public byte getCode() {
        return this.code;
    }
}
