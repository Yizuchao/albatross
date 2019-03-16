package com.yogi.albatross.command;

import java.util.List;

public class UnsubscribeCommand extends BaseCommand {
    private int packetId;
    private List<String> topics;

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
}
