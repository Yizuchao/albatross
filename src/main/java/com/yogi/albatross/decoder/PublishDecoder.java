package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.PublishQos;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.PublishRequest;
import com.yogi.albatross.utils.ThreadPoolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.math.NumberUtils;

@Processor(targetType = FixedHeadType.PUBLISH)
public class PublishDecoder extends DecoderAdapter{
    @Override
    protected BaseRequest process0(SimpleEncapPacket packet) throws Exception {
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
        if(retain==NumberUtils.INTEGER_ONE){//TODO 保留标志为1且有效载荷为零字节的PUBLISH报文会被服务端当作正常消息处理

        }
        if(retain==NumberUtils.INTEGER_ZERO){//TODO 如果客户端发给服务端的PUBLISH报文的保留标志位0，服务端不能存储这个消息也不能移除或替换任何现存的保留消息

        }

        ByteBuf byteBuf=packet.getByteBuf();
        int topicNameLen=byteBuf.readUnsignedShort();
        request.setTopicName(readUTF(byteBuf,topicNameLen));

        int packtIdLen= NumberUtils.INTEGER_ONE;
        if(PublishQos.ONE.equals(qos)|| PublishQos.TWO.equals(qos)){
            packtIdLen=byteBuf.readUnsignedShort();
            request.setPacketId(packtIdLen);
        }
        //固定报头中的剩余长度字段的值减去可变报头的长度
        String payload=readUTF(byteBuf,packet.getLen()-topicNameLen-packtIdLen);
        request.setPayload(payload);
        return request;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, BaseRequest request) throws Exception {
        if(request!=null){
            PublishRequest publishRequest=(PublishRequest)request;
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
                    ThreadPoolUtils.execute(()->{
                        //TODO action 服务端使用PUBLISH报文发送应用消息给每一个订阅匹配的客户端
                        ctx.writeAndFlush(bytes);
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

        }
        return null;
    }
}
