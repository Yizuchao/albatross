package com.yogi.albatross.db.server.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.common.server.ServerSessionProto.ServerSession;

/**
 * 保存服务器的运行信息
 */
@Dao
public class ServerDao {
    public Integer saveSession(String userId, ServerSession session){
        //TODO
        return Integer.valueOf(1);
    }
    public ServerSession getSessionFromDb(Long userId){
        try{
            byte[] bytes=new byte[1000];//TODO
            ServerSession.parseFrom(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
