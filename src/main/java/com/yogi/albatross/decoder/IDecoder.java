package com.yogi.albatross.decoder;

import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import io.netty.channel.ChannelHandlerContext;

public interface IDecoder{
    BaseRequest process(SimpleEncapPacket packet) throws Exception;

    byte[] response(ChannelHandlerContext ctx,BaseRequest request) throws Exception;
}
