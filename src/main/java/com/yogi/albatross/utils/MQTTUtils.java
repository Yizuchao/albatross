package com.yogi.albatross.utils;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MQTTUtils {
    public static final byte variableLenMark = 0b01111111;

    public static int parseLength(ByteBuf byteBuf) throws Exception{
        int readableSize=byteBuf.readableBytes();
        if(readableSize<=0){
            return 0;
        }
        byte first=byteBuf.readByte();
        if((first | variableLenMark)!=-1){
            return (int)first;
        }
        byte second=byteBuf.readByte();
        if((second | variableLenMark)!=-1){
            return (first & 0x7f) <<7  | second;
        }
        byte third=byteBuf.readByte();
        if((third | variableLenMark)!=-1){
            return (first & 0x7f) << 14 | (second & 0x7f) <<7 | third;
        }
        byte four=byteBuf.readByte();
        if((four | variableLenMark)!=-1){
            return (first & 0x7f) << 21 | (second & 0x7f) <<14 | (third &0x7f) <<7 | four;
        }
        throw new Exception("the format of packet remaining length error");
    }
}
