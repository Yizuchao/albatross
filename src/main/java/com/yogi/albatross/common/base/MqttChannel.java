package com.yogi.albatross.common.base;

import com.yogi.albatross.db.server.entity.UserSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 *
 */
public class MqttChannel {
    private Channel parent;
    public MqttChannel(Channel parent) {
        this.parent = parent;
    }

    public UserSession getUserSession() {
        Attribute<UserSession> attr = parent.attr(AttributeKey.valueOf(this.id().asLongText()));
        return attr.get();
    }


    public ChannelId id(){
        return parent.id();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key){
        return parent.attr(key);
    }
}
