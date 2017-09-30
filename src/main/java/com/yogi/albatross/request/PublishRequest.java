package com.yogi.albatross.request;

import com.yogi.albatross.constants.common.PublishQos;

public class PublishRequest extends BaseRequest{
    private int dup;
    private PublishQos qos;
    private int retain;
    private String topicName;
    private int packetId;
    private String payload;

    public int getDup() {
        return dup;
    }

    public void setDup(int dup) {
        this.dup = dup;
    }

    public PublishQos getQos() {
        return qos;
    }

    public void setQos(PublishQos qos) {
        this.qos = qos;
    }

    public int getRetain() {
        return retain;
    }

    public void setRetain(int retain) {
        this.retain = retain;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
