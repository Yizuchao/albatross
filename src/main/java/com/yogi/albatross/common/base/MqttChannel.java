package com.yogi.albatross.common.base;

import com.yogi.albatross.common.server.ServerSessionProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 *
 */
public class MqttChannel {
    private Channel parent;
    private ServerSessionProto.ServerSession serverSession;
    public MqttChannel(Channel parent) {
        this.parent = parent;
    }

    public void setServerSession(ServerSessionProto.ServerSession serverSession) {
        this.serverSession = serverSession;
    }

    public ServerSessionProto.ServerSession getServerSession() {
        return serverSession;
    }

    public ChannelId id(){
        return parent.id();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key){
        return parent.attr(key);
    }
}
