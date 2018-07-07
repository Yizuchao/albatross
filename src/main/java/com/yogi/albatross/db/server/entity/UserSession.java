package com.yogi.albatross.db.server.entity;

import com.yogi.albatross.common.server.ServerSessionProto;
import com.yogi.albatross.request.ConnectRequest;

import java.util.Date;

public class UserSession {
    private int id;
    private Long userId;
    private ServerSessionProto.ServerSession serverSession;
    private String willTopic;
    private String willMessage;
    private Date createTime;
    private Date lastUpdateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ServerSessionProto.ServerSession getServerSession() {
        return serverSession;
    }

    public void setServerSession(ServerSessionProto.ServerSession serverSession) {
        this.serverSession = serverSession;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }


}
