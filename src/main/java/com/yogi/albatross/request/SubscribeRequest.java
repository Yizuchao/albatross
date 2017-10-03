package com.yogi.albatross.request;

import java.util.List;

public class SubscribeRequest extends BaseRequest{
    private int packetId;
    private List<String> topics;
    private List<Integer> qos;

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<Integer> getQos() {
        return qos;
    }

    public void setQos(List<Integer> qos) {
        this.qos = qos;
    }
}
