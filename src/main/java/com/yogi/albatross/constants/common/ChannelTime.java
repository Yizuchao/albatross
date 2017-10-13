package com.yogi.albatross.constants.common;

import io.netty.channel.Channel;

public class ChannelTime {
    private int keepAlive;
    private Channel channel;

    public ChannelTime(int keepAlive, Channel channel) {
        this.keepAlive = keepAlive;
        this.channel = channel;
    }
}
