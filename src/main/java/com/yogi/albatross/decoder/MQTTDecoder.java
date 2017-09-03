package com.yogi.albatross.decoder;

import com.google.common.collect.ArrayListMultimap;
import com.yogi.albatross.Starter;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.processor.IProcessor;
import com.yogi.albatross.utils.ClassUtils;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.MQTTUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MQTTDecoder extends ByteToMessageDecoder {
    private static final Logger logger= LoggerFactory.getLogger(MQTTDecoder.class);
    private final ArrayListMultimap processors = ArrayListMultimap.create();

    public MQTTDecoder() {
        init();
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readableSize = byteBuf.readableBytes();
        SimpleEncapPacket simpleEncapPacket=new SimpleEncapPacket(ctx,byteBuf,list);
        if (readableSize > 0) {
            byte headCode = byteBuf.readByte();
            simpleEncapPacket.setType(FixedHeadType.valueOf(headCode));
            simpleEncapPacket.setLen(MQTTUtils.parseLength(byteBuf));
        }
    }

    private void doDecode(FixedHeadType type, ByteBuf byteBuf) {
        ;
    }

    private void init() {
        try{
            List<Class<?>> classList = ClassUtils.getClassList(Starter.class.getPackage().getName(), true, Processor.class);
            if(!CollectionUtils.isEmpty(classList)){
                int size=classList.size();
                for (int i = 0; i < size; i++) {
                    Class<?> clazz=classList.get(i);
                    if(clazz.isAssignableFrom(IProcessor.class)){
                        Processor processorAnnotation = clazz.getAnnotation(Processor.class);
                        processors.put(processorAnnotation.targetType(),clazz.newInstance());
                    }
                }
            }
        }catch (Exception e){
            logger.error("init mqtt decoder occur error",e);
        }
    }
}
