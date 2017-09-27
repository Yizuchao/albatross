package com.yogi.albatross.decoder;

import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

public abstract class DecoderAdapter implements IDecoder {
    private static final int DEFAULT_UTF_LEN=2;

    @Override
    public BaseRequest process(SimpleEncapPacket packet) throws Exception {
        try{
            return process0(packet);
        }finally {
            boolean release = packet.getByteBuf().release();
            while (!release){//释放bytebuf
                release=packet.getByteBuf().release();
            }
        }
    }
    protected abstract BaseRequest process0(SimpleEncapPacket packet) throws Exception;

    protected String readUTF(ByteBuf byteBuf, int len){
        return new String(readBytes(byteBuf,len), CharsetUtil.UTF_8);
    }

    protected String readUTF(ByteBuf byteBuf){
        return readUTF(byteBuf,DEFAULT_UTF_LEN);
    }

    protected byte[] readBytes(ByteBuf byteBuf, int len){
        byte[] bytes=new byte[len];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
