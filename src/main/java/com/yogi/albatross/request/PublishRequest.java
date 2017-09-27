package com.yogi.albatross.request;

public class PublishRequest extends BaseRequest{
    private int dup;
    private int qos;
    private int retain;
    private String topicName;
    private String packetId;
    private String payload;

    public int getDup() {
        return dup;
    }

    public void setDup(int dup) {
        this.dup = dup;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
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

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
