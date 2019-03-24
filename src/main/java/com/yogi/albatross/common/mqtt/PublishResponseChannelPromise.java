package com.yogi.albatross.common.mqtt;

import io.netty.channel.DefaultChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishResponseChannelPromise extends DefaultChannelPromise {
    private static final Logger logger=LoggerFactory.getLogger(PublishResponseChannelPromise.class);

    public PublishResponseChannelPromise(MqttChannel channel) {
        super(channel.channel());
        super.addListener(future -> {
            if (!future.isSuccess()) {
                logger.error(future.cause().getMessage(),future.cause());
            }
        });
    }
}
