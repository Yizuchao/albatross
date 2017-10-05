package com.yogi.albatross.utils;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.math.NumberUtils;
import sun.misc.Unsafe;

public class MQTTUtils {
    public static final byte variableLenMark = 0b01111111;

    /**
     *解析长度
     * @param byteBuf
     * @return
     * @throws Exception
     */
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

    /**
     * 该长度占用字节数
     * @param len
     * @return
     * @throws Exception
     */
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

    /**
     * 固定头部占用字节数
     * @param len
     * @return
     * @throws Exception
     */
    public static int fixedHeaderBytes(int len) throws Exception{
        return lengthBytes(len)+ NumberUtils.INTEGER_ONE;
    }

    /**
     * 断开连接byte数组
     * @return
     */
    public static byte[] disconnectBytes(){
        byte[] bytes=new byte[2];
        bytes[0]=(byte) 0xe0;
        bytes[1]=(byte) 0x00;
        return bytes;
    }

    /**
     * 将长度解析成字节数组
     * @param len
     * @return
     */
    public static byte[] lengthToBytes(int len) throws Exception{
        if(len<=0x7F){
            byte[] bytes=new byte[1];
            bytes[0]=(byte)len;
            return bytes;
        }
        if(len<=0xff7f && len>=0x8001){
            byte[] bytes=new byte[2];
            bytes[0]=(byte) ((len >>7) & 0xff);
            bytes[1]=(byte)(0x007f & len);
            return bytes;
        }
        if(len<=0xffff7f && len>=0x808001){
            byte[] bytes=new byte[3];
            bytes[0]=(byte)((len >>14) & 0xff);
            bytes[1]=(byte)((len >>7) & 0x00ff);
            bytes[2]=(byte)(len & 0x00007f);
            return bytes;
        }
        if(len<=0xffffff7f && len>=0x80808001){
            byte[] bytes=new byte[4];
            bytes[0]=(byte)((len>>21) & 0xff);
            bytes[1]=(byte)((len>>14) & 0x00ff);
            bytes[2]=(byte)((len>>7) & 0x0000ff);
            bytes[3]=(byte)(len & 0x0000007f);
            return bytes;
        }
        throw new Exception("length too long ");
    }
}
