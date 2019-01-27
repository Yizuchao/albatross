package com.yogi.albatross.common.base;

import com.yogi.albatross.constants.common.Constants;
import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.message.dao.ClientMessageDao;
import com.yogi.albatross.utils.ThreadPoolUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.DefaultChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PublishMsgChannelPromise extends DefaultChannelPromise {
    private final Logger logger=LoggerFactory.getLogger(PublishMsgChannelPromise.class);
    private ClientMessageDao clientMessageDao;

    public PublishMsgChannelPromise(MqttChannel toChannel,Long messageId,String cliendId,byte[] messageContent) {
        super(toChannel.channel());
        new PublishMsgChannelPromise(toChannel,messageId,cliendId,messageContent,1);
    }

    public PublishMsgChannelPromise(MqttChannel toChannel,Long messageId,String cliendId,byte[] messageContent,long retryCount) {
        super(toChannel.channel());
        clientMessageDao=DaoManager.getDao(ClientMessageDao.class);
        super.addListener(future -> {
            if (!future.isSuccess()) {
                if(Objects.nonNull(messageId)){
                    clientMessageDao.updateRetry(cliendId,messageId,retryCount);
                }
                if(retryCount>Constants.RETRY_COUNT){
                    logger.error(future.cause().getMessage(),future.cause());
                }else {
                    logger.error(future.cause().getMessage(),future.cause());
                    ThreadPoolUtils.deplayExcute(()->{
                        toChannel.writeAndFlush(PooledByteBufAllocator.DEFAULT.directBuffer(messageContent.length).writeBytes(messageContent),
                                new PublishMsgChannelPromise(toChannel,messageId,cliendId,messageContent,retryCount+1));
                    },retryCount*Constants.RETRY_BASE);
                }
            }
        });
    }
}
