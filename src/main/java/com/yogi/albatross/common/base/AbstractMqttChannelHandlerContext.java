package com.yogi.albatross.common.base;


import com.yogi.albatross.common.server.ServerSessionProto;
import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.server.dao.UserSessionDao;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.apache.commons.lang3.math.NumberUtils;

public abstract class AbstractMqttChannelHandlerContext {
    protected final ChannelHandlerContext ctx;
    protected final MqttChannel channel;
    private UserSessionDao userSessionDao;

    public AbstractMqttChannelHandlerContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.channel = new MqttChannel(ctx.channel());
        this.userSessionDao = DaoManager.getDao(UserSessionDao.class);
    }

    public ChannelFuture close() {
        ServerSessionProto.ServerSession serverSession = channel.getServerSession();
        if (serverSession != null) {
            if (serverSession.getWillFalg() == NumberUtils.INTEGER_ONE) {
                //TODO publish will  message
            }
            if (serverSession.getWillRetain() != NumberUtils.INTEGER_ONE) {
                userSessionDao.clearWill(Long.parseLong(serverSession.getUserId()));
            }
        }
        return ctx.close();
    }

    public MqttChannel channel() {
        return this.channel;
    }

    public ChannelPipeline pipeline() {
        return ctx.pipeline();
    }
}
