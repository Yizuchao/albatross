package com.yogi.albatross.decoder;

import com.google.common.collect.Maps;
import com.yogi.albatross.Starter;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.utils.ClassUtils;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.MQTTUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MQTTDispatchDecoder extends ByteToMessageDecoder {
    private static final Logger logger= LoggerFactory.getLogger(MQTTDispatchDecoder.class);
    private final static Map<FixedHeadType,IDecoder> processors = Maps.newHashMap();

    static {
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
                SimpleEncapPacket simpleEncapPacket=new SimpleEncapPacket(ctx,frame,list);
                simpleEncapPacket.setHeadByte(headCode);
                simpleEncapPacket.setLen(len);

                IDecoder decoder=processors.get(FixedHeadType.valueOf(headCode));
                if(decoder==null){//不合法或者不支持的报文
                    ctx.close();
                }
                BaseRequest request = decoder.process(simpleEncapPacket);

                byte[] bytes=decoder.response(ctx,request);
                if(bytes!=null && bytes.length>0){//立即返回响应报文
                    ByteBuf buffer=ctx.alloc().directBuffer();
                    buffer.writeBytes(bytes);
                    ctx.writeAndFlush(buffer);
                }
            }
        }
    }

    /**
     * 分包处理
     * @param in
     */
    private ByteBuf frameDecode(int len,ChannelHandlerContext ctx,ByteBuf in) throws Exception{
        int readableSize=in.readableBytes();
        if(readableSize<len){//还没有足够的数据。等待ByteToMessageDecoder的cumulator自动累积数据
            return null;
        }

        //分包
        int readerIndex=in.readerIndex();
        int fixedHeaderLen=MQTTUtils.lengthBytes(len)+ NumberUtils.INTEGER_ONE;
        ByteBuf frame=in.retainedSlice(readerIndex+fixedHeaderLen,len);
        in.readerIndex(MQTTUtils.fixedHeaderBytes(len)+len);
        return frame;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private static void init() {
        try{
            List<Class<?>> classList = ClassUtils.getClassList(Starter.class.getPackage().getName(), true, Processor.class);
            if(!CollectionUtils.isEmpty(classList)){
                int size=classList.size();
                for (int i = 0; i < size; i++) {
                    Class<?> clazz=classList.get(i);
                    if(IDecoder.class.isAssignableFrom(clazz)){
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
