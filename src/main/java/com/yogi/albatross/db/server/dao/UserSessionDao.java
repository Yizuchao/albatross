package com.yogi.albatross.db.server.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.common.server.ServerSessionProto.ServerSession;
import com.yogi.albatross.db.server.entity.UserSession;
import com.yogi.albatross.utils.DbUtils;
import com.yogi.albatross.utils.MD5Utils;
import com.yogi.albatross.utils.SqlUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * 保存服务器的运行信息
 */
@Dao
public class UserSessionDao {
    private static final String SAVE_SQL="insert into server_session(userId,serverSession,willTopic,willMessage,createTime,lastUpdateTime) values (?,?,?,?,?,?)";
    private static final String SELECT_BY_USERID="select * from server_session where userId=?";
    private static final String CLEAR_WILL="update server_session set willTopic=null,willMessage=null,lastUpdateTime=? where userId=?";
    public Integer saveSession(UserSession userSession){
        try {
            Date now=new Date();
            DbUtils.insert(SAVE_SQL,userSession.getUserId(),userSession.getServerSession(),userSession.getWillTopic(),userSession.getWillMessage(),now,now);
            return NumberUtils.INTEGER_ONE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NumberUtils.INTEGER_ZERO;
    }
    public UserSession getSessionFromDb(Long userId){
        ResultSet rs=null;
        try{
            rs = DbUtils.select(SELECT_BY_USERID, userId);
            if(rs.next()){
                UserSession session=new UserSession();
                session.setCreateTime(rs.getDate("createTime"));
                session.setLastUpdateTime(rs.getDate("lastUpdateTime"));
                session.setUserId(rs.getLong("userId"));
                session.setId(rs.getInt("id"));
                session.setWillTopic(rs.getString("willTopic"));
                session.setWillMessage(rs.getString("willMessage"));
                session.setServerSession(ServerSession.parseFrom(MD5Utils.hexStrToBytes(rs.getString("serverSession"))));
                return session;
            }
        }catch (Exception e){
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException innere) {
                    innere.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除遗嘱
     */
    public void clearWill(Long userId){
        try {
            DbUtils.update(CLEAR_WILL,new Date(),userId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
