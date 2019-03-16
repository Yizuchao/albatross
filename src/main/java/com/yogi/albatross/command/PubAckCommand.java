package com.yogi.albatross.command;

public class PubAckCommand extends BaseCommand{
    private String packetId;

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }
}
