package com.yogi.albatross.utils;

import io.netty.util.CharsetUtil;

import java.security.MessageDigest;

public class MD5Utils {
    private static final char[]  HEX_VALUE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static final String encode(String str) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return bytesToHexStr(md5.digest(str.getBytes(CharsetUtil.UTF_8))).toLowerCase();
    }

    public static final String bytesToHexStr(byte[] bArray) {
        if (bArray == null || bArray.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        for (int i = 0; i < bArray.length; i++) {
            int highIndex=(bArray[i]>>4) & 0xF;
            sb.append(HEX_VALUE[highIndex]);
            int lowIndex=bArray[i] & 0xf;
            sb.append(HEX_VALUE[lowIndex]);
        }
        return sb.toString();
    }

    public static final byte[] hexStrToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        byte[] d = new byte[hexString.length()>>1];
        int pos=0;
        for (int i=0;i<hexString.length();i=i+2){
            d[pos++]=hexToByte(hexString.charAt(i),hexString.charAt(i+1));
        }
        return d;
    }

    private static byte hexToByte(char highChar,char lowChar){
        int len=HEX_VALUE.length;
        int high=-1;
        int low=-1;
        for (int i=0;i<len;i++){
            if(high<0 && HEX_VALUE[i]==highChar){
                high=i;
            }
            if(low<0 && HEX_VALUE[i]==lowChar){
                low=i;
            }
            if(high!=-1 && low!=-1){
                break;
            }
        }
        if(high==-1 || low==-1){
            throw  new RuntimeException("it's not a hex string");
        }
        return (byte) ((high <<4) | low);
    }


    public static void main(String args[]) throws Exception {
        byte[] bytes="123asvavsloolgrogsege呵呵呵pawfawfwPH".getBytes();
        String hexStr=bytesToHexStr(bytes);
        System.out.println(hexStr);
        byte[] bytes1 = hexStrToBytes(hexStr);
        System.out.println(new String(bytes1));
    }
}
