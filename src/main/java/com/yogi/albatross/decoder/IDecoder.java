package com.yogi.albatross.decoder;

import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.request.BaseRequest;

public interface IDecoder<T extends BaseRequest>{
    T process(MqttCommand packet) throws Exception;

    byte[] response(AbstractMqttChannelHandlerContext ctx, T request) throws Exception;
}
