package com.yogi.albatross.common.mqtt;

import com.yogi.albatross.db.server.entity.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 *
 */
public final class MqttChannel {
    private Channel parent;
    private volatile boolean unsubscribe;

    public MqttChannel(Channel parent) {
        this.parent = parent;
    }

    public Session getSession() {
        Attribute<Session> attr = parent.attr(AttributeKey.valueOf(clientId()));
        return attr.get();
    }

    public ChannelId channelId() {
        return parent.id();
    }

    public void write(Object o, ChannelPromise promise) {
        parent.write(o, promise);
    }

    public String clientId() {
        return getSession().getServerSession().getClientId();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return parent.attr(key);
    }

    public Channel channel() {
        return parent;
    }
    public void unsubscribe(){
        unsubscribe=false;
    }
    public boolean isSubscribe(){
        return !unsubscribe;
    }
}
