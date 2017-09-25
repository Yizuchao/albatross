package com.yogi.albatross.utils;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.math.NumberUtils;
import sun.misc.Unsafe;

public class MQTTUtils {
    public static final byte variableLenMark = 0b01111111;

    public static int parseLength(ByteBuf byteBuf) throws Exception{
        int readableSize=byteBuf.readableBytes();
        if(readableSize<=0){
            return 0;
        }
        byte first=byteBuf.getByte(1);
        if((first | variableLenMark)!=-1){
            return (int)first;
        }
        byte second=byteBuf.getByte(2);
        if((second | variableLenMark)!=-1){
            return (first & 0x7f) <<7  | second;
        }
        byte third=byteBuf.getByte(3);
        if((third | variableLenMark)!=-1){
            return (first & 0x7f) << 14 | (second & 0x7f) <<7 | third;
        }
        byte four=byteBuf.getByte(4);
        if((four | variableLenMark)!=-1){
            return (first & 0x7f) << 21 | (second & 0x7f) <<14 | (third &0x7f) <<7 | four;
        }
        throw new Exception("the format of packet remaining length error");
    }
    public static int lengthBytes(int len) throws Exception{
        if(len<=0x7F){
            return 1;
        }
        if(len<=0xff7f && len>=0x8001){
            return 2;
        }
        if(len<=0xffff7f && len>=0x808001){
            return 3;
        }
        if(len<=0xffffff7f && len>=0x80808001){
            return 4;
        }
        throw new Exception("too long");
    }
    public static int fixedHeaderBytes(int len) throws Exception{
        return lengthBytes(len)+ NumberUtils.INTEGER_ONE;
    }
}
