package com.yogi.albatross.decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerIdleStateHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.fillInStackTrace();
        ctx.close();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){//过期
            //TODO 判断需不需要保存session
            ctx.close();
        }
    }
}
