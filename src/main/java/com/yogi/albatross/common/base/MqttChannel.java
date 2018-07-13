package com.yogi.albatross.common.base;

import com.yogi.albatross.db.server.entity.UserSession;
import com.yogi.albatross.db.topic.dto.SubscribeDto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Objects;

/**
 *
 */
public class MqttChannel {
    private Channel parent;
    private List<SubscribeDto> newest100Topics;
    public MqttChannel(Channel parent) {
        this.parent = parent;
    }

    public List<SubscribeDto> getNewest100Topics() {
        return newest100Topics;
    }

    public void setNewest100Topics(List<SubscribeDto> newest100Topics) {
        this.newest100Topics = newest100Topics;
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

    public Long getCurrentUserId(){
        UserSession userSession = getUserSession();
        if(Objects.nonNull(userSession)){
            return userSession.getUserId();
        }
        return null;
    }
}
