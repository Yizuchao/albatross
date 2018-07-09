package com.yogi.albatross.common.base;

import com.yogi.albatross.common.server.ServerSessionProto;
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
    private UserSession userSession;
    public MqttChannel(Channel parent) {
        this.parent = parent;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public ChannelId id(){
        return parent.id();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key){
        return parent.attr(key);
    }
}
