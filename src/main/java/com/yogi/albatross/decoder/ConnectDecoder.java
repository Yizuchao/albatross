package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.common.base.AbstractMqttChannelHandlerContext;
import com.yogi.albatross.common.base.MqttChannel;
import com.yogi.albatross.common.server.ServerSessionProto;
import com.yogi.albatross.constants.ack.ConnAck;
import com.yogi.albatross.constants.common.Constants;
import com.yogi.albatross.constants.common.WillQos;
import com.yogi.albatross.constants.common.FixedHeadType;
import com.yogi.albatross.constants.common.MqttCommand;
import com.yogi.albatross.db.DaoManager;
import com.yogi.albatross.db.server.dao.SessionDao;
import com.yogi.albatross.db.server.entity.Session;
import com.yogi.albatross.db.topic.dao.TopicDao;
import com.yogi.albatross.db.user.dao.UserDao;
import com.yogi.albatross.db.user.dto.UserDto;
import com.yogi.albatross.request.ConnectRequest;
import io.netty.buffer.ByteBuf;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@Processor(targetType = FixedHeadType.CONNECT)
public class ConnectDecoder extends DecoderAdapter<ConnectRequest> {
    private UserDao dao;
    private SessionDao serverDao;
    private TopicDao topicDao;

    public ConnectDecoder() {
        dao= DaoManager.getDao(UserDao.class);
        serverDao=DaoManager.getDao(SessionDao.class);
        topicDao=DaoManager.getDao(TopicDao.class);
    }

    @Override
    public ConnectRequest process0(MqttCommand packet) throws Exception {
        String protocolName=readUTF(packet.getByteBuf());
        int protocolLevel=packet.getByteBuf().readByte();
        if(!Constants.PTOTOCOL_NAME.contains(protocolName) || Constants.PROTOCOL_LEVEL<protocolLevel){
            throw  new Exception("protocol["+protocolName+"] or protocol level["+protocolLevel+"] not support");
        }
        ConnectRequest connectRequest=payload(packet,keepLive(packet,connectFlags(packet,null)));
        return connectRequest;
    }

    private ConnectRequest connectFlags(MqttCommand packet, ConnectRequest connectRequest) throws Exception{
        if(connectRequest==null){
            connectRequest=new ConnectRequest();
        }
        //clean session
        byte flags=packet.getByteBuf().readByte();
        if((flags & 0x02)!=0){
            connectRequest.setClearSession(true);
        }else{
            connectRequest.setClearSession(false);
        }
        //Will Flag and Qos
        if((flags & 0x04)!=0){
            connectRequest.setWillFlag(NumberUtils.INTEGER_ONE);
            int willQos=flags & 0x18;
            if(willQos>2){//willQos must be {0,1,2}。
                throw  new Exception("willQos flag error");
            }
            connectRequest.setWillQos(WillQos.valueOf(willQos));
        }else {
            connectRequest.setWillFlag(NumberUtils.INTEGER_ZERO);
            int willQos=flags & 0x18;
            if(willQos!=0){//willQos must be {0}。
                throw  new Exception("willQos flag error");
            }
            connectRequest.setWillQos(WillQos.valueOf(willQos));
        }
        //Will Retain
        if((flags & 0x20)==0){
            connectRequest.setWillRetain(NumberUtils.INTEGER_ZERO);
        }else{
            connectRequest.setWillRetain(NumberUtils.INTEGER_ONE);
        }
        //User Name and password Flag
        int usernameFlag=flags & 0x80;
        int passwordFlag=flags & 0x40;
        if(usernameFlag==0){
            if(passwordFlag!=0){
                throw  new Exception("用户名标志被设置为0，密码标志也必须设置为0");
            }
            connectRequest.setUsernameFlag(false);
        }else{
            connectRequest.setUsernameFlag(true);
        }
        if(passwordFlag==0){//Password Flag
            connectRequest.setPasswordFlag(false);
        }else{
            connectRequest.setPasswordFlag(true);
        }
        return connectRequest;
    }

    /**
     * 添加空闲链路检测
     * @param packet
     * @param connectRequest
     * @return
     * @throws Exception
     */
    private ConnectRequest keepLive(MqttCommand packet, ConnectRequest connectRequest) throws Exception{
        short requestKeepLiveTime=packet.getByteBuf().readShort();
        connectRequest.setKeepLiveSecond(requestKeepLiveTime);
        //空闲链路检测
        int keepLiveTime=Math.max(10,requestKeepLiveTime);
        packet.getCtx().pipeline().addLast(new IdleStateHandler(keepLiveTime,keepLiveTime,keepLiveTime));
        return connectRequest;
    }
    private  ConnectRequest payload(MqttCommand packet, ConnectRequest connectRequest) throws Exception{
        ByteBuf byteBuf=packet.getByteBuf();
        //client id
        connectRequest.setClientId(readUTF(byteBuf));

        if(connectRequest.getWillFlag()==NumberUtils.INTEGER_ONE){
            connectRequest.setWillTopic(readUTF(byteBuf));
            connectRequest.setWillTopic(readUTF(byteBuf));
        }

        if(connectRequest.getUsernameFlag()){
            connectRequest.setUsername(readUTF(byteBuf));
        }

        if(connectRequest.getPasswordFlag()){
            connectRequest.setPassword(readUTF(byteBuf));
        }
        return connectRequest;
    }

    @Override
    public byte[] response(AbstractMqttChannelHandlerContext ctx, ConnectRequest request) throws Exception {
        if(request!=null){
            byte[] bs=new byte[4];
            bs[0]=0x20;
            bs[1]=0x02;
            if(request.getAck()!=null){
                bs[2]=0x00;
                bs[3]=request.getAck().getCode();
            }else {
                if(!Constants.ANONYMOUSE_SUPPORT && StringUtils.isEmpty(request.getUsername())){
                    return null;
                }
                if(request.getClearSession()){//清除session
                    clearSession(ctx,request.getClientId());
                }

                boolean usernameOrPsw=false;
                UserDto userDto = dao.selectByUsername(request.getUsername());
                if(userDto!=null && userDto.getPassword().equals(request.getPassword())){
                    dao.updateLastLoginTime(request.getUsername());

                    bs[3]= ConnAck.OK.getCode();
                    usernameOrPsw=true;
                    boolean hasRecovery=false;
                    if(!request.getClearSession()){//没有设置清除session，则尝试恢复session
                        hasRecovery=recoverySession(ctx, request);
                    }
                    if(request.getClearSession() || !hasRecovery){
                        createSession(ctx,request,userDto.getId());
                    }
                }else {
                    bs[3]=ConnAck.ERROR_USERNAME_OR_PSW.getCode();
                }

                if(usernameOrPsw && true){
                    bs[2]=0x01;
                }else {
                    bs[2]=0x00;
                }
            }
            return bs;
        }
        ctx.close();
        return null;
    }

    /**
     *  创建session
     * @param ctx
     * @param request
     */
    private Session createSession(AbstractMqttChannelHandlerContext ctx, ConnectRequest request, Long userId){
        MqttChannel channel=ctx.channel();
        ServerSessionProto.ServerSession.Builder builder = ServerSessionProto.ServerSession.newBuilder();
        builder.setKeepLiveSecond(request.getKeepLiveSecond()*1000);
        builder.setWillQos(request.getWillQos().getCode());
        builder.setWillFalg(request.getWillFlag());
        builder.setWillRetain(request.getWillRetain());
        builder.setClearSession(request.getClearSession());
        builder.setClientId(request.getClientId());
        builder.setUserId(userId);
        builder.setWillMessage(request.getWillMessage());
        builder.setWillTopic(request.getWillTopic());
        ServerSessionProto.ServerSession serverSession=builder.build();
        Attribute<ServerSessionProto.ServerSession> attr = channel.attr(AttributeKey.valueOf(request.getClientId()));
        attr.set(serverSession);

        Session session =new Session();
        session.setServerSession(serverSession);
        session.setClientId(request.getClientId());
        return session;
    }

    /**
     * 恢复session
     * @param ctx
     * @param request
     */
    private boolean recoverySession(AbstractMqttChannelHandlerContext ctx,ConnectRequest request){
        Session session =serverDao.getSessionFromDb(request.getClientId());
        if(session !=null){
            //保存遗嘱消息
            ServerSessionProto.ServerSession.Builder builder = ServerSessionProto.ServerSession.newBuilder(session.getServerSession());
            builder.setWillMessage(request.getWillMessage());
            builder.setWillTopic(request.getWillTopic());
            ServerSessionProto.ServerSession serverSession=builder.build();
            session.setServerSession(serverSession);
            serverDao.saveOrUpdateSession(session);

            MqttChannel channel=ctx.channel();
            Attribute<ServerSessionProto.ServerSession> attr = channel.attr(AttributeKey.valueOf(session.getServerSession().getClientId()));
            attr.set(serverSession);
            return true;
        }
        return false;
    }


    /**
     *   清除session
     * @param ctx
     */
    private void clearSession(AbstractMqttChannelHandlerContext ctx,String clientId){
        MqttChannel channel=ctx.channel();
        Attribute<ServerSessionProto.ServerSession> attr = channel.attr(AttributeKey.valueOf(clientId));
        attr.set(null);
    }
}
