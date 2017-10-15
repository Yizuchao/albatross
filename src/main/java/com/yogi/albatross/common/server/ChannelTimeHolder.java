package com.yogi.albatross.common.server;


import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

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
}
