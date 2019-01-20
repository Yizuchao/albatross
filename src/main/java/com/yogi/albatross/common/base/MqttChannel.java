package com.yogi.albatross.common.base;

import com.yogi.albatross.db.server.entity.UserSession;
import com.yogi.albatross.db.topic.dto.SubscribeDto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;
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

    public UserSession getUserSession() {
        Attribute<UserSession> attr = parent.attr(AttributeKey.valueOf(this.id().asLongText()));
        return attr.get();
    }

    public void writeAndFlush(Object o, ChannelPromise promise){
        parent.writeAndFlush(o,promise);
    }

    public void writeAndFlush(Object o){
        parent.writeAndFlush(o);
    }

    public ChannelId id(){
        return parent.id();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key){
        return parent.attr(key);
    }

    public Long getCurrentUserId(){
        UserSession userSession = getUserSession();
        if(Objects.nonNull(userSession)){
            return userSession.getUserId();
        }
        return null;
    }
    protected Channel channel(){
        return parent;
    }

    public void setParent(Channel parent) {
        this.parent = parent;
    }

    public void setUnscribed(boolean unscribed) {
        this.unscribed = unscribed;
    }

    public boolean isUnscribed() {
        return unscribed;
    }
}
