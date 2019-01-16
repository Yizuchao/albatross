package com.yogi.albatross.common.server;

import com.yogi.albatross.common.base.MqttChannelHandlerContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerIdleStateHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger=LoggerFactory.getLogger(ServerIdleStateHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(),cause);
        ctx.close();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent idleStateEvent=(IdleStateEvent)evt;
            if(IdleState.ALL_IDLE.equals(idleStateEvent.state())){
                logger.info(String.format("close the remote client[%s] by there is no data read or write in the limit time.",
                        ctx.channel().remoteAddress().toString()));
                MqttChannelHandlerContext.wrapper(ctx).close();
            }
            if(IdleState.WRITER_IDLE.equals(idleStateEvent.state())){
                logger.info(String.format("close the remote client[%s] by there is no data write in the limit time.",
                        ctx.channel().remoteAddress().toString()));
                MqttChannelHandlerContext.wrapper(ctx).close();
            }
            if(IdleState.READER_IDLE.equals(idleStateEvent.state())){
                logger.info(String.format("close the remote client[%s] by there is no data read in the limit time.",
                        ctx.channel().remoteAddress().toString()));
                MqttChannelHandlerContext.wrapper(ctx).close();
            }
        }
    }
}
