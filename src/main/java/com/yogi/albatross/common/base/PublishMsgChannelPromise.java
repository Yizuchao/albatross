package com.yogi.albatross.common.base;

import io.netty.channel.DefaultChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishMsgChannelPromise extends DefaultChannelPromise {
    private final Logger logger=LoggerFactory.getLogger(PublishMsgChannelPromise.class);

    public PublishMsgChannelPromise(MqttChannel toChannel) {
        super(toChannel.channel());
        super.addListener(future -> {
            if (!future.isSuccess()) {
                logger.error(future.cause().getMessage(),future.cause());
            }
        });
    }
}
