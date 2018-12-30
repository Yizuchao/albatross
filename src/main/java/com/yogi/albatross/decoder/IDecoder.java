package com.yogi.albatross.decoder;

import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import io.netty.channel.ChannelHandlerContext;

public interface IDecoder<T extends BaseRequest>{
    T process(SimpleEncapPacket packet) throws Exception;

    byte[] response(AbstractMqttChannelHandlerContext ctx, T request) throws Exception;
}
