package com.yogi.albatross.decoder;

import com.google.common.collect.Lists;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.FixedHeadType;
import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.request.UnsubscribeRequest;

import java.util.List;

@Processor(targetType = FixedHeadType.UNSUBSCRIBE)
public class UnsubscribeDecoder extends DecoderAdapter<UnsubscribeRequest>{

    @Override
    protected UnsubscribeRequest process0(MqttCommand packet) throws Exception {
        UnsubscribeRequest unsubscribeRequest=new UnsubscribeRequest();
        unsubscribeRequest.setPacketId(packet.getByteBuf().readUnsignedShort());
        List<String> topics= Lists.newArrayListWithExpectedSize(5);
        while (packet.getByteBuf().readableBytes()>0){
            topics.add(readUTF(packet.getByteBuf()));
        }
        unsubscribeRequest.setTopics(topics);
        return unsubscribeRequest;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, UnsubscribeRequest unsubscribeRequest) throws Exception {
        //TODO 它必须停止分发任何新消息给这个客户端
        //它必须完成分发任何已经开始往客户端发送的QoS 1和QoS 2的消息
        //它可以继续发送任何现存的准备分发给客户端的缓存消息
        ctx.channel().setUnscribed(true);
        byte[] bytes=new byte[4];
        bytes[0]=(byte)0xb0;
        bytes[1]=(byte)0x02;
        bytes[2]=(byte) ((0xff00 &unsubscribeRequest.getPacketId())>>8);
        bytes[3]=(byte) (0x00ff &unsubscribeRequest.getPacketId());
        return bytes;
    }
}
