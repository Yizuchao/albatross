package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.mqtt.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.FixedHeadType;
import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.command.PubrelCommand;

/**
 * “发布释放”报文处理类
 */
@Processor(targetType = FixedHeadType.PUBREL)
public class PubrelDecoder extends DecoderAdapter<PubrelCommand>{
    @Override
    protected PubrelCommand process0(MqttCommand packet) throws Exception {
        PubrelCommand request=new PubrelCommand();
        request.setPacketId(packet.getByteBuf().readUnsignedShort());
        return request;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, PubrelCommand pubrelRequest) throws Exception {
        byte[] bytes=new byte[4];
        bytes[0]=0x40;
        bytes[1]=0x02;
        bytes[2]=(byte)((pubrelRequest.getPacketId() &0xff00)>>8);//取高8位
        bytes[3]=(byte)(pubrelRequest.getPacketId() &0x00ff);//取低8位
        return bytes;
    }
}
