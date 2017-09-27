package com.yogi.albatross.decoder;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.ack.ConnAck;
import com.yogi.albatross.constants.ack.IAck;
import com.yogi.albatross.constants.common.Constants;
import com.yogi.albatross.constants.common.WillQos;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import com.yogi.albatross.request.BaseRequest;
import com.yogi.albatross.request.ConnectRequest;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

@Processor(targetType = FixedHeadType.CONNECT)
public class ConnectDecoder extends DecoderAdapter {
    @Override
    public BaseRequest process0(SimpleEncapPacket packet) throws Exception {
        String protocolName=protocolName(packet.getByteBuf());
        int protocolLevel=protocolLevel(packet.getByteBuf());
        if(!Constants.PTOTOCOL_NAME.contains(protocolName) || Constants.PROTOCOL_LEVEL<protocolLevel){
            throw  new Exception("protocol["+protocolName+"] or protocol level["+protocolLevel+"] not support");
        }
        ConnectRequest connectRequest=payload(packet,keepLive(packet,connectFlags(packet,null)));
        return connectRequest;
    }

    private String protocolName(ByteBuf byteBuf){
        byteBuf.skipBytes(1);//discard MSB byte
        byte protocolLen=byteBuf.readByte();
        byte[] bytes=new byte[protocolLen];
        byteBuf.readBytes(bytes,0,protocolLen);
        return new String(bytes, CharsetUtil.UTF_8);
    }

    private int protocolLevel(ByteBuf byteBuf){
        return byteBuf.readByte();
    }

    private ConnectRequest connectFlags(SimpleEncapPacket packet,ConnectRequest connectRequest) throws Exception{
        if(connectRequest==null){
            connectRequest=new ConnectRequest();
        }
        byte flags=packet.getByteBuf().readByte();
        if((flags & 0x02)!=0){//clean session
            connectRequest.setClearSession(true);
            //TODO clear old session and create a new session
        }else{
            connectRequest.setClearSession(false);
            //TODO recovery session
        }
        if((flags & 0x04)!=0){//Will Flag
            connectRequest.setWillFlag(true);
            int willQos=flags & 0x18;
            if(willQos>2){//willQos must be {0,1,2}。
                throw  new Exception("willQos flag error");
            }
            connectRequest.setWillQos(WillQos.valueOf(willQos));
        }else {
            connectRequest.setWillFlag(false);
            int willQos=flags & 0x18;
            if(willQos!=0){//willQos must be {0}。
                throw  new Exception("willQos flag error");
            }
            connectRequest.setWillQos(WillQos.valueOf(willQos));
        }
        if((flags & 0x20)==0){//Will Retain
            connectRequest.setWillRetain(true);
            //TODO 服务端必须将遗嘱消息当作非保留消息发布
        }else{
            connectRequest.setWillRetain(false);
            //TODO 服务端必须将遗嘱消息当作保留消息发布
        }
        int usernameFlag=flags & 0x80;
        int passwordFlag=flags & 0x40;
        if(usernameFlag==0){//User Name Flag
            if(passwordFlag!=0){
                throw  new Exception("用户名标志被设置为0，密码标志也必须设置为0");
            }
            connectRequest.setUsernameFlag(false);
            //TODO 有效载荷中不能包含用户名字段
        }else{
            connectRequest.setUsernameFlag(true);
            //TODO 有效载荷中必须包含用户名字段
        }
        if(passwordFlag==0){//Password Flag
            connectRequest.setPasswordFlag(false);
            //TODO 有效载荷中不能包含密码字段
        }else{
            connectRequest.setPasswordFlag(true);
            //TODO 有效载荷中必须包含密码字段
        }
        return connectRequest;
    }
    private ConnectRequest keepLive(SimpleEncapPacket packet,ConnectRequest connectRequest) throws Exception{
        connectRequest.setKeepLiveSecond(packet.getByteBuf().readShort());
        return connectRequest;
    }
    private  ConnectRequest payload(SimpleEncapPacket packet,ConnectRequest connectRequest) throws Exception{
        ByteBuf byteBuf=packet.getByteBuf();
        //client id
        connectRequest.setClientId(readUTF(byteBuf));

        if(connectRequest.getWillFlag()){
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

    private String readUTF(ByteBuf byteBuf){
        int len=byteBuf.readUnsignedShort();
        return super.readUTF(byteBuf,len);
    }

    @Override
    public byte[] response(BaseRequest request, IAck ack) throws Exception {
        byte[] bs=new byte[4];
        bs[0]=0x20;
        bs[1]=0x02;
        if(ack!=null){
            bs[2]=0x00;
            bs[3]=ack.getCode();
        }else {
            ConnectRequest cr=(ConnectRequest) request;
            boolean usernameOrPsw=false;
            if("yogi".equals(cr.getUsername())&& "123456".equals(cr.getPassword())){//TODO
                bs[3]= ConnAck.OK.getCode();
                usernameOrPsw=true;
            }else {
                bs[3]=ConnAck.ERROR_USERNAME_OR_PSW.getCode();
            }

            if(usernameOrPsw && true){//TODO 服务端保存了会话状态
                bs[2]=0x01;
            }else {
                bs[2]=0x00;
            }
        }
        return bs;
    }
}
