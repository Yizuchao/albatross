package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.SubscribeRequest;
import io.netty.channel.ChannelHandlerContext;

@Processor(targetType = FixedHeadType.SUBSCRIBE)
public class SubscribeDecoder extends DecoderAdapter {
    @Override
    protected BaseRequest process0(SimpleEncapPacket packet) throws Exception {
        SubscribeRequest request = new SubscribeRequest();
        request.setPacketId(packet.getByteBuf().readUnsignedShort());
        request.setPayload(readBytes(packet.getByteBuf(), packet.getLen() - 2));
        return request;
    }

    @Override
    public byte[] response(ChannelHandlerContext ctx, BaseRequest request) throws Exception {
        SubscribeRequest subscribeRequest=(SubscribeRequest)request;
        int i=0;
        for (;;){
            i++;
        }
        return new byte[0];
    }
}
