package com.yogi.albatross.db.message.entity;

import com.yogi.albatross.db.common.Status;

public class Message {
    private byte[] content;
    private Status sended;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Status getSended() {
        return sended;
    }

    public void setSended(Status sended) {
        this.sended = sended;
    }
}
