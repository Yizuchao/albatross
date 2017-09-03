package com.yogi.albatross.processor;

import com.yogi.albatross.annotation.Processor;
import com.yogi.albatross.constants.common.Constants;
import com.yogi.albatross.constants.head.FixedHeadType;
import com.yogi.albatross.constants.packet.SimpleEncapPacket;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

@Processor(targetType = FixedHeadType.CONNECT)
public class ConnectProcessor implements IProcessor{
    @Override
    public void process(SimpleEncapPacket packet) throws Exception {
        if(!protocolCheck(packet.getByteBuf())){
            throw  new Exception("protocol or protocol level not support");
        }
    }

    private boolean protocolCheck(ByteBuf byteBuf){
        byteBuf.readByte();//discard MSB byte
        byte protocolLen=byteBuf.readByte();
        byte[] bytes=new byte[protocolLen];
        byteBuf.readBytes(bytes,0,protocolLen);
        String protocolName=new String(bytes, CharsetUtil.UTF_8);
        byte protocolLevel=byteBuf.readByte();
        return Constants.PTOTOCOL_NAME.equals(protocolName) && protocolLevel<=Constants.PROTOCOL_LEVEL;
    }
    private void connectFlags(SimpleEncapPacket packet) throws Exception{
        byte flags=packet.getByteBuf().readByte();
        if((flags & 0x02)!=0){
            //TODO clear old session and create a new session
        }else{
            //TODO recovery session
        }
    }
}
