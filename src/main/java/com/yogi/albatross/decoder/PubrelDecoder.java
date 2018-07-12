package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.PubrelRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * “发布释放”报文处理类
 */
@Processor(targetType = FixedHeadType.PUBREL)
public class PubrelDecoder extends DecoderAdapter{
    @Override
    protected BaseRequest process0(SimpleEncapPacket packet) throws Exception {
        PubrelRequest request=new PubrelRequest();
        request.setPacketId(packet.getByteBuf().readUnsignedShort());
        return request;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, BaseRequest request) throws Exception {
        PubrelRequest pubrelRequest=(PubrelRequest)request;
        byte[] bytes=new byte[4];
        bytes[0]=0x40;
        bytes[1]=0x02;
        bytes[2]=(byte)((pubrelRequest.getPacketId() &0xff00)>>8);//取高8位
        bytes[3]=(byte)(pubrelRequest.getPacketId() &0x00ff);//取低8位
        return bytes;
    }
}
