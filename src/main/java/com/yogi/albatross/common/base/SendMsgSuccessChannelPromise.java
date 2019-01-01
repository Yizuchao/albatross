package com.yogi.albatross.common.base;

import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class SendMsgSuccessChannelPromise extends DefaultChannelPromise {
    private byte[] reponse;
    private MqttChannel mqttChannel;

    public SendMsgSuccessChannelPromise(MqttChannel mqttChannel, byte[] response) {
        super(mqttChannel.channel());
        this.reponse = response;
        this.mqttChannel=mqttChannel;
    }

    private class DefaultListener implements GenericFutureListener {

        @Override
        public void operationComplete(Future future) throws Exception {
            if (future.isSuccess()) {
                mqttChannel.writeAndFlush(reponse);
            }else {
                //TODO   消息推送失败了
            }
        }
    }
}
