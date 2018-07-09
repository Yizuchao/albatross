package com.yogi.albatross.db.server.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.common.server.ServerSessionProto;
import com.yogi.albatross.common.server.ServerSessionProto.ServerSession;
import com.yogi.albatross.db.server.entity.UserSession;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.DbUtils;
import com.yogi.albatross.utils.MD5Utils;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 保存服务器的运行信息
 */
@Dao
public class UserSessionDao {
    private static final String SAVE ="insert into server_session(userId,serverSession,willTopic,willMessage,createTime,lastUpdateTime) values (?,?,?,?,?,?)";
    private static final String UPDATE="update server_session set serverSession=?,willTopic=?,willMessage=?,lastUpdateTime=?";
    private static final String SAVE_WILL="insert into server_session(userId,willTopic,willMessage,createTime,lastUpdateTime) values(?,?,?,?,?)";
    private static final String SELECT_BY_USERID="select * from server_session where userId=?";
    private static final String CLEAR_WILL="update server_session set willTopic=null,willMessage=null,lastUpdateTime=? where userId=?";
    public Integer saveOrUpdateSession(UserSession userSession){
        try {
            Date now=new Date();
            String sessionHex=MD5Utils.bytesToHexStr(userSession.getServerSession().toByteArray());
            if(userSession.getId()==0){
                List<Integer> resultList =DbUtils.insert(SAVE,userSession.getUserId(),sessionHex,userSession.getWillTopic(),userSession.getWillMessage(),now,now);
                if(!CollectionUtils.isEmpty(resultList)){
                    return resultList.get(NumberUtils.INTEGER_ZERO);
                }
            }else {
                DbUtils.update(UPDATE,sessionHex,userSession.getWillTopic(),userSession.getWillMessage(),now);
                return NumberUtils.INTEGER_ONE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NumberUtils.INTEGER_ZERO;
    }

    public Integer saveWill(Long userId,String willTopic,String willMessage){
        try{
            Date now=new Date();
            List<Integer> ids = DbUtils.insert(SAVE_WILL, userId, willTopic, willMessage, now, now);
            if(!CollectionUtils.isEmpty(ids)){
                return ids.get(NumberUtils.INTEGER_ZERO);
            }
        }catch (Exception e){
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
