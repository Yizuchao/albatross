package com.yogi.albatross.common.base;

import com.yogi.albatross.db.server.entity.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Objects;

/**
 *
 */
public final class MqttChannel {
    private Channel parent;
    private volatile  boolean unscribed;

    public MqttChannel(Channel parent) {
        this.parent = parent;
    }

    public Session getSession() {
        Attribute<Session> attr = parent.attr(AttributeKey.valueOf(clientId()));
        return attr.get();
    }
    public ChannelId channelId(){
        return parent.id();
    }

    public void writeAndFlush(Object o, ChannelPromise promise){
        parent.writeAndFlush(o,promise);
    }

    public void writeAndFlush(Object o){
        parent.writeAndFlush(o);
    }

    public String clientId(){
        return getSession().getServerSession().getClientId();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key){
        return parent.attr(key);
    }

    protected Channel channel(){
        return parent;
    }

    public void setUnscribed(boolean unscribed) {
        this.unscribed = unscribed;
    }

    public boolean isUnscribed() {
        return unscribed;
    }
}
