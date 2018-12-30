package com.yogi.albatross.decoder;

import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.db.server.entity.UserSession;
import com.yogi.albatross.request.BaseRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import com.yogi.albatross.common.server.ServerSessionProto.ServerSession;

public abstract class DecoderAdapter<T extends BaseRequest> implements IDecoder<T> {

    @Override
    public T process(SimpleEncapPacket packet) throws Exception {
        try{
            return process0(packet);
        }catch (Exception e){
            e.printStackTrace();
            packet.getCtx().close();
            return null;
        }
        finally {
            boolean release = packet.getByteBuf().release();
            while (!release){//释放bytebuf
                release=packet.getByteBuf().release();
            }
        }
    }
    protected abstract T process0(SimpleEncapPacket packet) throws Exception;

    protected String readUTF(ByteBuf byteBuf, int len){
        return new String(readBytes(byteBuf,len), CharsetUtil.UTF_8);
    }

    protected String readUTF(ByteBuf byteBuf){
        return readUTF(byteBuf,byteBuf.readUnsignedShort());
    }

    protected byte[] readBytes(ByteBuf byteBuf, int len){
        byte[] bytes=new byte[len];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    protected Long currentUser(ChannelHandlerContext ctx){
        return getSession(ctx).getUserId();
    }

    protected UserSession getSession(ChannelHandlerContext ctx){
        return (UserSession) ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().asLongText())).get();
    }
}
