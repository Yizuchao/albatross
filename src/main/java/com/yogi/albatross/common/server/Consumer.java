package com.yogi.albatross.common.server;

import com.yogi.albatross.common.mqtt.MqttChannel;
import io.netty.channel.ChannelPromise;

import java.io.Serializable;
import java.util.Iterator;

public class Consumer implements Serializable {
    private static final long serialVersionUID = 1L;

    private MqttChannel mqttChannel;
    private Iterator<MessageProto.Message> msgIt;

    public Consumer(MqttChannel mqttChannel, Iterator<MessageProto.Message> msgIt) {
        this.mqttChannel = mqttChannel;
        this.msgIt = msgIt;
    }

    public boolean isSubscribe(){
        return mqttChannel.isSubscribe();
    }

    public void write(Object o, ChannelPromise promise) {
        mqttChannel.write(o, promise);
    }

    public MqttChannel mqttChannel() {
        return mqttChannel;
    }
}
