package com.yogi.albatross.common.base;


import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.server.dao.UserSessionDao;
import com.yogi.albatross.db.server.entity.UserSession;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.apache.commons.lang3.math.NumberUtils;

public abstract class AbstractMqttChannelHandlerContext {
    private final ChannelHandlerContext ctx;
    private final MqttChannel channel;
    private UserSessionDao userSessionDao;

    protected AbstractMqttChannelHandlerContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.channel = new MqttChannel(ctx.channel());
        this.userSessionDao = DaoManager.getDao(UserSessionDao.class);

    }

    public ChannelFuture close() {
        UserSession userSession = channel.getUserSession();
        if (userSession != null) {
            boolean saveSession = !userSession.getServerSession().getClearSession();
            if (saveSession) {
                userSessionDao.saveOrUpdateSession(userSession);
            }
            if (userSession.getServerSession().getWillFalg() == NumberUtils.INTEGER_ONE) {
                //TODO publish will  message
                if (!saveSession && userSession.getServerSession().getWillRetain() != NumberUtils.INTEGER_ONE) {//not specify to retain will,then clear will
                    clearWill(userSession);
                }
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

    private boolean clearWill(UserSession userSession) {
        userSession.setWillTopic(null);
        userSession.setWillMessage(null);
        userSessionDao.clearWill(userSession.getUserId());
        return true;
    }
    public ChannelFuture writeAndFlush(Object msg){
        return ctx.writeAndFlush(msg);
    }
}
