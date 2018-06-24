package com.yogi.albatross.common.server;


import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * 保存每个channel过期时间
 */
public class ChannelTimeHolder {
    private static final ConcurrentMap<String,ChannelTime> map= Maps.newConcurrentMap();

    public static void put(ChannelTime channelTime){
        String id=channelTime.getChannel().id().asLongText();
        map.put(id,channelTime);
    }
    public static void  put(String channelId,ChannelTime channelTime){
        map.put(channelId,channelTime);
    }
    public static ChannelTime get(String channelId){
        ChannelTime channelTime = map.get(channelId);
        if(channelTime.getExpireTime()>System.currentTimeMillis()){
            return channelTime;
        }else {
            map.remove(channelId);
            channelTime.getChannel().close();
            return null;
        }
    }
    public static boolean remove(String channelId){
        try{
            map.remove(channelId);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
