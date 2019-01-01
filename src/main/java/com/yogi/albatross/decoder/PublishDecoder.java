package com.yogi.albatross.decoder;

import com.google.protobuf.ByteString;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.common.base.MqttChannel;
import com.yogi.albatross.common.base.SendMsgSuccessChannelPromise;
import com.yogi.albatross.common.server.MessageProto;
import com.yogi.albatross.common.server.ServerTopics;
import com.yogi.albatross.constants.common.PublishQos;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.PublishRequest;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.MessageIdGenerateUtils;
import com.yogi.albatross.utils.ThreadPoolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Processor(targetType = FixedHeadType.PUBLISH)
public class PublishDecoder extends DecoderAdapter<PublishRequest>{
    @Override
    protected PublishRequest process0(SimpleEncapPacket packet) throws Exception {
        PublishRequest request=new PublishRequest();
        byte headByte=packet.getHeadByte();
        PublishQos qos=PublishQos.valueOf(headByte & 0x06);
        int dup=headByte & 0x08;
        request.setQos(qos);
        request.setDup(dup);
        if(PublishQos.ZERO.equals(qos) && dup!=NumberUtils.INTEGER_ZERO){
            throw new Exception("QoS 0的消息，DUP标志必须设置为0 ");
        }

        int retain=headByte & 0x01;
        request.setRetain(retain);
        ByteBuf byteBuf=packet.getByteBuf();
        int topicNameLen=byteBuf.readUnsignedShort();
        request.setTopicName(readUTF(byteBuf,topicNameLen));
        int packtIdLen= NumberUtils.INTEGER_ONE;
        if(PublishQos.ONE.equals(qos)|| PublishQos.TWO.equals(qos)){
            packtIdLen=byteBuf.readUnsignedShort();
            request.setPacketId(packtIdLen);
        }
        //固定报头中的剩余长度字段的值减去可变报头的长度
        request.setPayload(readBytes(byteBuf,packet.getLen()-topicNameLen-packtIdLen));
        return request;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, PublishRequest publishRequest) throws Exception {
        if(Objects.isNull(publishRequest)){
            return null;
        }
        if(NumberUtils.INTEGER_ONE.equals(publishRequest.getRetain()) && (publishRequest.isPayloadEmpty()
               || PublishQos.ZERO.equals(publishRequest.getQos()))){
            clearTopicRetainMsg();
        }
        if(NumberUtils.INTEGER_ONE.equals(publishRequest.getRetain()) && PublishQos.ZERO.equals(publishRequest.getQos())){
            saveTopicRetainMsg();
        }
        switch (publishRequest.getQos()){
            case ZERO:{
                return null;
            }
            case ONE:{
                byte[] bytes=new byte[4];
                bytes[0]=0x40;
                bytes[1]=0x02;
                bytes[2]=(byte)((publishRequest.getPacketId() &0xff00)>>8);//取高8位
                bytes[3]=(byte)(publishRequest.getPacketId() &0x00ff);//取低8位
                MessageProto.Message message = getMessage(publishRequest, true, String.valueOf(ctx.getCurrentUserId()));
                ThreadPoolUtils.execute(()->{
                    //TODO 持久化消息
                    sendMsg(publishRequest.getTopicName(),Arrays.asList(message),new SendMsgSuccessChannelPromise(ctx.channel(),bytes));
                });
                return null;
            }
            case TWO:{
                byte[] bytes=new byte[4];
                bytes[0]=0x50;
                bytes[1]=0x02;
                bytes[2]=(byte)((publishRequest.getPacketId() &0xff00)>>8);//取高8位
                bytes[3]=(byte)(publishRequest.getPacketId() &0x00ff);//取低8位
                return bytes;//返回"发布收到"报文
            }
        }
        return null;
    }

    //清除主题下面保留的消息
    private void clearTopicRetainMsg(){
        //TODO
    }
    private void saveTopicRetainMsg(){
        //TODO
    }
    public void sendMsg(String topicOrQueueName,List<MessageProto.Message> msgs,ChannelPromise channelPromise){
        List<MqttChannel> channels = ServerTopics.searchSubscriber(topicOrQueueName);
        if(CollectionUtils.isEmpty(channels)){
            return;
        }
        channels.forEach(mqttChannel -> {
            mqttChannel.writeAndFlush(msgs,channelPromise);
        });
    }

    private MessageProto.Message getMessage(PublishRequest publishRequest,boolean isTopic,String currentUser){
        MessageProto.Message.Builder builder=MessageProto.Message.newBuilder();
        builder.setMessageId(MessageIdGenerateUtils.messageId());
        builder.setContent(ByteString.copyFrom(publishRequest.getPayload()));
        builder.setType(isTopic?MessageProto.Message.Type.TOPIC:MessageProto.Message.Type.QUEUE);
        builder.setFrom(currentUser);
        return builder.build();
    }
}
