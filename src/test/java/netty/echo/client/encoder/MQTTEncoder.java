package netty.echo.client.encoder;

import com.yogi.albatross.utils.MQTTUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MQTTEncoder {
    public static byte[] encodeLength(int length) throws Exception{
        byte[] bytes=null;
        if(length<128){
            bytes=new byte[1];
            bytes[0]=(byte)length;
            return bytes;
        }
        if(length<=0x3fff){
            bytes=new byte[2];
            bytes[1]= (byte)(length & 0x7f);
            bytes[0]=(byte)(length >>>7 & 0x007f | 0x80);
            return bytes;
        }
        if(length<=0x1fffff){
            bytes=new byte[3];
            bytes[2]=(byte)(length & 0x7f);
            bytes[1]=(byte)(length >>>7 & 0x007f | 0x80);
            bytes[0]=(byte)(length >>>14 & 0x007f | 0x80);
            return bytes;
        }
        if(length<=0x0fffffff){
            bytes=new byte[4];
            bytes[3]=(byte)(length & 0x7f);
            bytes[2]=(byte)(length >>>7 & 0x007f | 0x80);
            bytes[1]=(byte)(length >>>14 & 0x007f | 0x80);
            bytes[0]=(byte)(length >>>21 & 0x007f | 0x80);
            return bytes;
        }
        throw new Exception("message too long .max size:256MB");
    }
    public static void main(String[] args) throws Exception{
        byte[] bytes = encodeLength(268335455);
        ByteBuf byteBuf=Unpooled.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        System.out.println(MQTTUtils.parseLength(byteBuf));
    }
}
