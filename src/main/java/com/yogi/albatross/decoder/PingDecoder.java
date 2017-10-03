package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import io.netty.channel.ChannelHandlerContext;

@Processor(targetType = FixedHeadType.PINGREQ)
public class PingDecoder extends DecoderAdapter{
    @Override
    protected BaseRequest process0(SimpleEncapPacket packet) throws Exception {
        return null;
    }

    @Override
    public byte[] response(ChannelHandlerContext ctx, BaseRequest request) throws Exception {
        byte[] bytes=new byte[2];
        bytes[0]=(byte) 0xd0;
        bytes[1]=(byte)0x00;
        return bytes;
    }
}
