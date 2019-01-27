package com.yogi.albatross.common.base;


import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.server.dao.SessionDao;
import com.yogi.albatross.db.server.entity.Session;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import org.apache.commons.lang3.math.NumberUtils;

public abstract class AbstractMqttChannelHandlerContext {
    private final ChannelHandlerContext ctx;
    private final MqttChannel channel;
    private SessionDao sessionDao;

    protected AbstractMqttChannelHandlerContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.channel = new MqttChannel(ctx.channel());
        this.sessionDao = DaoManager.getDao(SessionDao.class);

    }

    public ChannelFuture close() {
        Session session = channel.getSession();
        if (session != null) {
            boolean saveSession = !session.getServerSession().getClearSession();
            if (saveSession) {
                sessionDao.saveOrUpdateSession(session);
            }
            if (session.getServerSession().getWillFalg() == NumberUtils.INTEGER_ONE) {
                //TODO publish will  message
                if (!saveSession && session.getServerSession().getWillRetain() != NumberUtils.INTEGER_ONE) {//not specify to retain will,then clear will
                    sessionDao.clearWill(session);
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

    public ChannelFuture writeAndFlush(Object msg){
        return ctx.writeAndFlush(msg);
    }

    public ChannelFuture writeAndFlush(Object msg,ChannelPromise promise){
        return ctx.writeAndFlush(msg,promise);
    }

    public String getClientId(){
        return channel.clientId();
    }

    public String clientId(){
        return channel.clientId();
    }
}
