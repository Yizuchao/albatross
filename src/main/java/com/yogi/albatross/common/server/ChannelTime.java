package com.yogi.albatross.common.server;

import io.netty.channel.Channel;

public class ChannelTime {
    private long expireTime;
    private Channel channel;

    public ChannelTime(long expireTime, Channel channel) {
        this.expireTime = expireTime;
        this.channel = channel;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public Channel getChannel() {
        return channel;
    }
}
