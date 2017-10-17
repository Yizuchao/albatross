package com.yogi.albatross.utils;

import io.netty.util.CharsetUtil;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;

public class MD5Utils {
    public static final String encode(String str) throws Exception{
        MessageDigest md5=MessageDigest.getInstance("MD5");
        return bytesToHexStr(md5.digest(str.getBytes(CharsetUtil.UTF_8))).toLowerCase();
    }

    public static final String bytesToHexStr(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static void main(String args[]) throws Exception{
        System.out.println(encode("123456"));
    }
}
