package com.yogi.albatross.request;

import com.yogi.albatross.constants.common.PublishQos;

import java.util.Objects;

public class PublishRequest extends BaseRequest{
    private int dup;
    private PublishQos qos;
    private int retain;
    private String topicName;
    private int packetId;
    private byte[] payload;

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

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public boolean isPayloadEmpty(){
        return Objects.isNull(payload) || payload.length==0;
    }
}
