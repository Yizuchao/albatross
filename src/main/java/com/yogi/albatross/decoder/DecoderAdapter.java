package com.yogi.albatross.decoder;

import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.command.BaseCommand;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DecoderAdapter<T extends BaseCommand> implements IDecoder<T> {
    protected final Logger logger=LoggerFactory.getLogger(this.getClass());

    @Override
    public T process(MqttCommand packet) throws Exception {
        try{
            return process0(packet);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            packet.getCtx().close();
            return null;
        } finally {
            ReferenceCountUtil.release(packet.getByteBuf());
        }
    }
    protected abstract T process0(MqttCommand packet) throws Exception;

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
}
