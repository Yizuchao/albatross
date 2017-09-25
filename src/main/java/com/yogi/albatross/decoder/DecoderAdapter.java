package com.yogi.albatross.decoder;

import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

public abstract class DecoderAdapter implements IDecoder {

    @Override
    public BaseRequest process(SimpleEncapPacket packet) throws Exception {
        try{
            return process0(packet);
        }finally {
            boolean release = packet.getByteBuf().release();
            while (!release){
                release=packet.getByteBuf().release();
            }
        }
    }
    protected abstract BaseRequest process0(SimpleEncapPacket packet) throws Exception;

    protected String readUTF(ByteBuf byteBuf, int len){
        byte[] bytes=new byte[len];
        byteBuf.readBytes(bytes);
        return new String(bytes, CharsetUtil.UTF_8);
    }
}
