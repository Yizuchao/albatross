package com.yogi.albatross.request;

import com.yogi.albatross.constants.ack.ConnAck;
import com.yogi.albatross.constants.common.WillQos;

public class ConnectRequest  extends BaseRequest{
    //flags
    private boolean clearSession;//是否清除session
    private boolean willFlag;//是否有遗嘱
    private WillQos willQos;//指定发布遗嘱消息时使用的服务质量等级
    private boolean willRetain;//遗嘱是否需要保留
    private boolean usernameFlag;//payload 是否包含username
    private boolean passwordFlag;//payload 是否包含password
    private short keepLiveSecond;//连接保存有效时间

    //payload
    private String clientId;
    private String willTopic;//遗嘱主题
    private String willMessage;//遗嘱消息内容
    private String username;
    private String password;

    private ConnAck ack;

    public boolean getClearSession() {
        return clearSession;
    }

    public void setClearSession(boolean clearSession) {
        this.clearSession = clearSession;
    }

    public boolean getWillFlag() {
        return willFlag;
    }

    public void setWillFlag(boolean willFlag) {
        this.willFlag = willFlag;
    }

    public WillQos getWillQos() {
        return willQos;
    }

    public void setWillQos(WillQos willQos) {
        this.willQos = willQos;
    }

    public boolean getWillRetain() {
        return willRetain;
    }

    public void setWillRetain(boolean willRetain) {
        this.willRetain = willRetain;
    }

    public boolean getUsernameFlag() {
        return usernameFlag;
    }

    public void setUsernameFlag(boolean usernameFlag) {
        this.usernameFlag = usernameFlag;
    }

    public boolean getPasswordFlag() {
        return passwordFlag;
    }

    public void setPasswordFlag(boolean passwordFlag) {
        this.passwordFlag = passwordFlag;
    }

    public short getKeepLiveSecond() {
        return keepLiveSecond;
    }

    public void setKeepLiveSecond(short keepLiveSecond) {
        this.keepLiveSecond = keepLiveSecond;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    public String getWillMessage() {
        return willMessage;
    }

    public void setWillMessage(String willMessage) {
        this.willMessage = willMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnAck getAck() {
        return ack;
    }

    public void setAck(ConnAck ack) {
        this.ack = ack;
    }

    @Override
    public String toString() {
        return "ConnectRequest{" +
                "clearSession=" + clearSession +
                ", willFlag=" + willFlag +
                ", willQos=" + willQos +
                ", willRetain=" + willRetain +
                ", usernameFlag=" + usernameFlag +
                ", passwordFlag=" + passwordFlag +
                ", keepLiveSecond=" + keepLiveSecond +
                ", clientId='" + clientId + '\'' +
                ", willTopic='" + willTopic + '\'' +
                ", willMessage='" + willMessage + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ack=" + ack +
                '}';
    }
}