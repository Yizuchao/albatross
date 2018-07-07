package com.yogi.albatross.common.base;

import io.netty.channel.ChannelHandlerContext;

/**
 * Decorator of ChannelHandlerContext
 */
public class MqttChannelHandlerContext extends AbstractMqttChannelHandlerContext {

    private final ChannelHandlerContext ctx;

    public MqttChannelHandlerContext(ChannelHandlerContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    public static AbstractMqttChannelHandlerContext wrapper(ChannelHandlerContext ctx){
        return new MqttChannelHandlerContext(ctx);
    }
}
