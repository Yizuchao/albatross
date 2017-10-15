package com.yogi.albatross.common.server;

public class ServerSession {
    private String userId;
    private String channelId;

    public String getUserId() {
        return userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
