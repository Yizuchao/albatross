package com.yogi.albatross.common.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMsgSuccessChannelPromise extends DefaultChannelPromise {
    private final Logger logger=LoggerFactory.getLogger(SendMsgSuccessChannelPromise.class);

    public SendMsgSuccessChannelPromise(MqttChannel fromChannel, MqttChannel toChannel, byte[] reponse) {
        super(toChannel.channel());
        super.addListener(future -> {
            if (future.isSuccess()) {
                ByteBuf respByteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(reponse.length).writeBytes(reponse);
                fromChannel.writeAndFlush(respByteBuf, new ResponseChannelPromise(fromChannel.channel()));
            } else {
                logger.error(future.cause().getMessage(),future.cause());
            }
        });
    }

    private class ResponseChannelPromise extends DefaultChannelPromise {

        public ResponseChannelPromise(Channel channel) {
            super(channel);
            super.addListener(future -> {
                if (!future.isSuccess()) {
                    logger.error(future.cause().getMessage(),future.cause());
                }
            });
        }
    }
}
