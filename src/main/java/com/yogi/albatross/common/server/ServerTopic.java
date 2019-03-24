package com.yogi.albatross.common.server;

import com.google.common.collect.Lists;
import com.yogi.albatross.common.mqtt.MqttChannel;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

public class ServerTopic {
    private final List<Consumer> consumers;
    private final TopicLinkedQueue msgQueue;

    public ServerTopic() {
        msgQueue=new TopicLinkedQueue();
        consumers=Lists.newArrayListWithCapacity(NumberUtils.INTEGER_ONE);
    }

    public void addConsumer(MqttChannel channel){
        synchronized (consumers){
            consumers.add(new Consumer(channel,msgQueue.iterator()));
        }
    }

    public List<Consumer> consumers(){
        return consumers;
    }
}
