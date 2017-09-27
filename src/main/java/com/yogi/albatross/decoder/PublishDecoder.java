package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.ack.IAck;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.PublishRequest;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.math.NumberUtils;

@Processor(targetType = FixedHeadType.PUBLISH)
public class PublishDecoder extends DecoderAdapter{
    @Override
    protected BaseRequest process0(SimpleEncapPacket packet) throws Exception {
        PublishRequest request=new PublishRequest();
        byte headByte=packet.getHeadByte();
        int qos=headByte & 0x06;
        int dup=headByte & 0x08;
        request.setQos(qos);
        request.setDup(dup);
        if(request.getQos()==0 && dup!=0){
            throw new Exception("QoS 0的消息，DUP标志必须设置为0 ");
        }

        int retain=headByte & 0x01;
        request.setRetain(retain);
        if(retain==1){//TODO 保留标志为1且有效载荷为零字节的PUBLISH报文会被服务端当作正常消息处理

        }
        if(retain==0){//TODO 如果客户端发给服务端的PUBLISH报文的保留标志位0，服务端不能存储这个消息也不能移除或替换任何现存的保留消息

        }

        ByteBuf byteBuf=packet.getByteBuf();
        int topicNameLen=byteBuf.readUnsignedShort();
        request.setTopicName(readUTF(byteBuf,topicNameLen));

        int packtIdLen= NumberUtils.INTEGER_ONE;
        if(qos==1 || qos==2){
            packtIdLen=byteBuf.readUnsignedShort();
            request.setPacketId(readUTF(byteBuf,packtIdLen));
        }
        //固定报头中的剩余长度字段的值减去可变报头的长度
        String payload=readUTF(byteBuf,packet.getLen()-topicNameLen-packtIdLen);
        request.setPayload(payload);
        return request;
    }

    @Override
    public byte[] response(BaseRequest request, IAck ack) throws Exception {
        return new byte[0];
    }
}
