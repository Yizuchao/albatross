package com.yogi.albatross.constants.packet;

import com.yogi.albatross.constants.head.FixedHeadType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class SimpleEncapPacket {
    private ChannelHandlerContext ctx;//context
    private List<Object> out;
    private FixedHeadType type;//packet type
    private int len;//packet content length
    private ByteBuf byteBuf;//surplus byteBuf

    public SimpleEncapPacket(ChannelHandlerContext ctx,ByteBuf byteBuf, List<Object> out) {
        this.ctx = ctx;
        this.out = out;
        this.byteBuf = byteBuf;
    }

    public FixedHeadType getType() {
        return type;
    }

    public void setType(FixedHeadType type) {
        this.type = type;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public List<Object> getOut() {
        return out;
    }
}
