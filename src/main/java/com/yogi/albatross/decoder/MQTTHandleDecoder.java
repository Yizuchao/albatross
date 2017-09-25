package com.yogi.albatross.decoder;

import com.google.common.collect.Maps;
import com.yogi.albatross.Starter;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.utils.ClassUtils;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.MQTTUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MQTTHandleDecoder extends ByteToMessageDecoder {
    private static final Logger logger= LoggerFactory.getLogger(MQTTHandleDecoder.class);
    private final Map<FixedHeadType,IDecoder> processors = Maps.newHashMap();

    public MQTTHandleDecoder() {
        init();
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readableSize = byteBuf.readableBytes();
        int len=MQTTUtils.parseLength(byteBuf);
        if (readableSize > 0) {
            byte headCode = byteBuf.getByte(0);
            ByteBuf frame=frameDecode(len,ctx,byteBuf);
            if(frame==null){//没有解析到数据
                return;
            }else {
                SimpleEncapPacket simpleEncapPacket=new SimpleEncapPacket(ctx,byteBuf,list);
                simpleEncapPacket.setType(FixedHeadType.valueOf(headCode));
                simpleEncapPacket.setLen(MQTTUtils.parseLength(frame));
                processors.get(simpleEncapPacket.getType()).process(simpleEncapPacket);
            }
        }
    }

    /**
     * 分包处理
     * @param in
     */
    private ByteBuf frameDecode(int len,ChannelHandlerContext ctx,ByteBuf in) throws Exception{
        int readableSize=in.readableBytes();
        if(len<readableSize){//还没有足够的数据。等待ByteToMessageDecoder的cumulator自动累积数据
            return null;
        }

        //分包
        int readerIndex=in.readerIndex();
        ByteBuf frame=in.retainedSlice(readerIndex,len);
        in.readerIndex(MQTTUtils.fixedHeaderBytes(len)+len);
        return frame;
    }

    private void init() {
        try{
            List<Class<?>> classList = ClassUtils.getClassList(Starter.class.getPackage().getName(), true, Processor.class);
            if(!CollectionUtils.isEmpty(classList)){
                int size=classList.size();
                for (int i = 0; i < size; i++) {
                    Class<?> clazz=classList.get(i);
                    if(clazz.isAssignableFrom(IDecoder.class)){
                        Processor processorAnnotation = clazz.getAnnotation(Processor.class);
                        processors.put(processorAnnotation.targetType(), (IDecoder) clazz.newInstance());
                    }
                }
            }
        }catch (Exception e){
            logger.error("init mqtt decoder occur error",e);
        }
    }
}
