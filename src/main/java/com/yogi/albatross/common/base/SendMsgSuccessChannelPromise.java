package com.yogi.albatross.common.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class SendMsgSuccessChannelPromise extends DefaultChannelPromise {

    public SendMsgSuccessChannelPromise(MqttChannel fromChannel, MqttChannel toChannel, byte[] reponse) {
        super(toChannel.channel());
        super.addListener(future -> {
            if (future.isSuccess()) {
                ByteBuf respByteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(reponse.length).writeBytes(reponse);
                fromChannel.writeAndFlush(respByteBuf, new ResponseChannelPromise(fromChannel.channel()));
            } else {
                future.cause().printStackTrace();
            }
        });
    }

    private class ResponseChannelPromise extends DefaultChannelPromise {

        public ResponseChannelPromise(Channel channel) {
            super(channel);
            super.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("replay sender success");
                } else {
                    future.cause().printStackTrace();
                }
            });
        }
    }
}
