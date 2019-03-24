package com.yogi.albatross.decoder;

import com.yogi.albatross.common.mqtt.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.command.BaseCommand;

public interface IDecoder<T extends BaseCommand>{
    T process(MqttCommand packet) throws Exception;

    byte[] response(AbstractMqttChannelHandlerContext ctx, T request) throws Exception;
}
