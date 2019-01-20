package com.yogi.albatross.decoder;

import com.google.protobuf.ByteString;
import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.common.base.MqttChannel;
import com.yogi.albatross.common.base.SendMsgSuccessChannelPromise;
import com.yogi.albatross.common.server.MessageProto;
import com.yogi.albatross.common.server.ServerTopics;
import com.yogi.albatross.constants.common.PublishQos;
import com.yogi.albatross.constants.common.FixedHeadType;
import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.common.Status;
import com.yogi.albatross.db.message.dao.MessageDao;
import com.yogi.albatross.db.message.entity.Message;
import com.yogi.albatross.request.PublishRequest;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.MessageIdGenerateUtils;
import com.yogi.albatross.utils.ThreadPoolUtils;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Objects;

@Processor(targetType = FixedHeadType.PUBLISH)
public class PublishDecoder extends DecoderAdapter<PublishRequest>{
    private MessageDao messageDao;
    public PublishDecoder() {
        messageDao=DaoManager.getDao(MessageDao.class);
    }

    private static final int PUBLISH_PACKAGE_FIX_LEN=4;//用于表示主题名长度的两个字节+用于表示报文标识符的长度
    @Override
    protected PublishRequest process0(MqttCommand packet) throws Exception {
        PublishRequest request=new PublishRequest();
        byte headByte=packet.getHeadByte();
        PublishQos qos=PublishQos.valueOf(headByte>>1 & 0x03);
        int dup=(headByte & 0x06)>>3;
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
        if(PublishQos.ONE.equals(qos)|| PublishQos.TWO.equals(qos)){
            int packtId=byteBuf.readUnsignedShort();
            request.setPacketId(packtId);
        }
        request.setPublishData(byteBuf.retainedDuplicate());
        request.setPayload(readBytes(byteBuf,packet.getLen()-topicNameLen-PUBLISH_PACKAGE_FIX_LEN));
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
                sendMsg(publishRequest.getTopicName(),publishRequest.getPublishData(),ctx,bytes);
                return null;
            }
            case TWO:{
                byte[] bytes=new byte[4];
                bytes[0]=0x50;
                bytes[1]=0x02;
                bytes[2]=(byte)((publishRequest.getPacketId() &0xff00)>>8);//取高8位
                bytes[3]=(byte)(publishRequest.getPacketId() &0x00ff);//取低8位
                sendMsg(publishRequest.getTopicName(),publishRequest.getPublishData(),ctx,bytes);
                return null;//返回"发布收到"报文
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
    public void sendMsg(String topicOrQueueName,ByteBuf publishData,AbstractMqttChannelHandlerContext ctx,byte[] response){
        ThreadPoolUtils.execute(()->{
            Message message=new Message();
            message.setContent(publishData.array());
            message.setSended(Status.OK);
            messageDao.save(message);

            List<MqttChannel> channels = ServerTopics.searchSubscriber(topicOrQueueName);
            if(CollectionUtils.isEmpty(channels)){
                return;
            }
            int size=channels.size();
            if(size>1){
                publishData.retain(size-1);
            }
            for (MqttChannel mqttChannel:channels){
                publishData.readerIndex(0);
                mqttChannel.writeAndFlush(publishData,new SendMsgSuccessChannelPromise(ctx.channel(),mqttChannel,response));
            }
        });
    }

    private MessageProto.Message getMessage(byte[] content,boolean isTopic,String currentUser){
        MessageProto.Message.Builder builder=MessageProto.Message.newBuilder();
        builder.setMessageId(MessageIdGenerateUtils.messageId());
        builder.setContent(ByteString.copyFrom(content));
        builder.setType(isTopic?MessageProto.Message.Type.TOPIC:MessageProto.Message.Type.QUEUE);
        builder.setFrom(currentUser);
        return builder.build();
    }
}
