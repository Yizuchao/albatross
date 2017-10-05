package com.yogi.albatross.decoder;

import com.google.common.collect.Lists;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.SubscribeRequest;
import com.yogi.albatross.utils.MQTTUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

@Processor(targetType = FixedHeadType.SUBSCRIBE)
public class SubscribeDecoder extends DecoderAdapter {
    @Override
    protected BaseRequest process0(SimpleEncapPacket packet) throws Exception {
        ByteBuf byteBuf=packet.getByteBuf();
        SubscribeRequest request = new SubscribeRequest();
        request.setPacketId(byteBuf.readUnsignedShort());
        List<String> topics= Lists.newArrayListWithExpectedSize(5);
        List<Integer> qos=Lists.newArrayListWithExpectedSize(5);
        while (byteBuf.readableBytes()>0) {
            topics.add(readUTF(packet.getByteBuf()));
            qos.add((int)packet.getByteBuf().readUnsignedByte());
        }
        request.setTopics(topics);
        request.setQos(qos);
        return request;
    }

    @Override
    public byte[] response(ChannelHandlerContext ctx, BaseRequest request) throws Exception {
        SubscribeRequest subscribeRequest=(SubscribeRequest)request;
        int topicsSize=subscribeRequest.getTopics().size();
        int variableHeaderLen=topicsSize+2;//话题长度+packetId长度
        byte[] lenBytes = MQTTUtils.lengthToBytes(variableHeaderLen);//长度字段数组

        byte[] bytes=new byte[lenBytes.length+1+variableHeaderLen];
        int index=0;
        bytes[index++]=(byte)0x90;//type

        //length bytes
        for (int i = 0; i < lenBytes.length; i++) {
            bytes[index=index+i]=lenBytes[i];
        }

        //packet id
        bytes[++index]=(byte) ((subscribeRequest.getPacketId() & 0xff00) >> 8);
        bytes[++index]=(byte) (subscribeRequest.getPacketId() & 0x00ff);

        //topic qos reponse
        for (int i=0;i<topicsSize;i++) {//TODO 每个返回码对应等待确认的SUBSCRIBE报文中的一个主题过滤器
            bytes[i+(++index)]=0x00;//默认返回成功
        }
        return bytes;
    }
}
