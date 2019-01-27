package com.yogi.albatross.common.base;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Decorator of ChannelHandlerContext
 */
public class MqttChannelHandlerContext extends AbstractMqttChannelHandlerContext {

    private static Map<ChannelId, MqttChannelHandlerContext> mqttCtxs = new ConcurrentHashMap();
    private MqttChannelHandlerContext(ChannelHandlerContext ctx) {
        super(ctx);
    }

    public static AbstractMqttChannelHandlerContext wrapper(ChannelHandlerContext ctx){
        MqttChannelHandlerContext mqttCtx = mqttCtxs.get(ctx.channel().id());
        if(mqttCtx==null){
            mqttCtx=new MqttChannelHandlerContext(ctx);
            mqttCtxs.put(ctx.channel().id(),mqttCtx);
        }
        return mqttCtx;
    }

    @Override
    public ChannelFuture close() {
        mqttCtxs.remove(this.channel().channelId());
        return super.close();
    }
}
