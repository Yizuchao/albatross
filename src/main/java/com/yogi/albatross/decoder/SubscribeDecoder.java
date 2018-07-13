package com.yogi.albatross.decoder;

import com.google.common.collect.Lists;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.SubscribeQos;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.topic.dao.TopicDao;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.SubscribeRequest;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.MQTTUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

@Processor(targetType = FixedHeadType.SUBSCRIBE)
public class SubscribeDecoder extends DecoderAdapter {
    private TopicDao topicDao;

    public SubscribeDecoder() {
        topicDao= DaoManager.getDao(TopicDao.class);
    }

    @Override
    protected BaseRequest process0(SimpleEncapPacket packet) throws Exception {
        ByteBuf byteBuf=packet.getByteBuf();
        SubscribeRequest request = new SubscribeRequest();
        request.setPacketId(byteBuf.readUnsignedShort());
        List<String> topics= Lists.newArrayListWithExpectedSize(5);
        List<SubscribeQos> qoss=Lists.newArrayListWithExpectedSize(5);
        while (byteBuf.readableBytes()>0) {
            topics.add(readUTF(packet.getByteBuf()));
            SubscribeQos qos =SubscribeQos.valueOf ((int)packet.getByteBuf().readUnsignedByte());
            if(qos==null){
                throw  new Exception("非法报文，qos非法");
            }
            qoss.add(qos);
        }
        if(CollectionUtils.isEmpty(topics)){//至少要包含一个主题，否则关闭链接
            packet.getCtx().close();
            throw new Exception("非法报文，至少包含一个主题名。");
        }else {
            request.setTopics(topics);
            request.setQos(qoss);
            return request;
        }
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, BaseRequest request) throws Exception {
        SubscribeRequest subscribeRequest=(SubscribeRequest)request;
        boolean saveSuccess=topicDao.saveOrSubscribe(subscribeRequest.getTopics(),ctx.getCurrentUserId(),subscribeRequest.getQos());

        //response bytes
        int topicsSize=subscribeRequest.getTopics().size();
        int variableHeaderLen=topicsSize+2;//话题长度+packetId长度
        byte[] lenBytes = MQTTUtils.lengthToBytes(variableHeaderLen);//长度字段数组

        byte[] bytes=new byte[lenBytes.length+NumberUtils.INTEGER_ONE+variableHeaderLen];
        int index= NumberUtils.INTEGER_ZERO;
        bytes[index++]=(byte)0x90;//type

        //length bytes
        for (int i = NumberUtils.INTEGER_ZERO; i < lenBytes.length; i++) {
            bytes[index++]=lenBytes[i];
        }

        //2 byte packet id
        bytes[index++]=(byte) ((subscribeRequest.getPacketId() & 0xff00) >> 8);
        bytes[index++]=(byte) (subscribeRequest.getPacketId() & 0x00ff);

        //topic qos reponse
        for (int i=0;i<topicsSize;i++) {//服务端可以授予客户端比客户端要求的qos的更低的qos；同时这里可以异步写response。这里就默认返回客户端要求的qos了。后面可以完善 //TODO
            bytes[index++]=saveSuccess?(byte) subscribeRequest.getQos().get(i).getCode():(byte) 0x80;
        }
        return bytes;
    }
}
