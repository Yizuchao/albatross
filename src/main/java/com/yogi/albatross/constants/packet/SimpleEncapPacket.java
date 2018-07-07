package com.yogi.albatross.constants.packet;

import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.common.base.MqttChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class SimpleEncapPacket {
    private AbstractMqttChannelHandlerContext ctx;//context
    private List<Object> out;
    private byte headByte;//headByte
    private int len;//packet content length
    private ByteBuf byteBuf;//surplus byteBuf

    public SimpleEncapPacket(ChannelHandlerContext ctx,ByteBuf byteBuf, List<Object> out) {
        this.ctx = new MqttChannelHandlerContext(ctx);
        this.out = out;
        this.byteBuf = byteBuf;
    }

    public byte getHeadByte() {
        return headByte;
    }

    public void setHeadByte(byte headByte) {
        this.headByte = headByte;
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

    public AbstractMqttChannelHandlerContext getCtx() {
        return ctx;
    }

    public List<Object> getOut() {
        return out;
    }
}
