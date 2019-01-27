package com.yogi.albatross.db.server.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.common.server.ServerSessionProto;
import com.yogi.albatross.common.server.ServerSessionProto.ServerSession;
import com.yogi.albatross.db.server.entity.Session;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.DbUtils;
import com.yogi.albatross.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 保存服务器的运行信息
 */
@Dao
public class SessionDao {
    private static final Logger logger=LoggerFactory.getLogger(SessionDao.class);
    private static final String SAVE_OR_UPDATE ="insert into session(clientId,serverSession) values (?,?) on duplicate key update serverSession=?";
    private static final String UPDATE="update session set serverSession=?,willTopic=?,willMessage=?,lastUpdateTime=?";
    private static final String SELECT_BY_CLIENTID="select * from session where clientId=?";
    private static final String CLEAR_WILL="update session set willTopic=null,willMessage=null where clientId=?";
    public Long saveOrUpdateSession(Session session){
        try {
            String sessionHex=MD5Utils.bytesToHexStr(session.getServerSession().toByteArray());
            List<Long> resultList =DbUtils.insert(SAVE_OR_UPDATE,session.getServerSession().getClientId(),sessionHex,sessionHex);
            if(!CollectionUtils.isEmpty(resultList)){
                return resultList.get(NumberUtils.INTEGER_ZERO);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return NumberUtils.LONG_ZERO;
    }

    public Session getSessionFromDb(String clientId){
        ResultSet rs=null;
        try{
            rs = DbUtils.select(SELECT_BY_CLIENTID, clientId);
            if(rs.next()){
                Session session=new Session();
                session.setId(rs.getInt("id"));
                String serverSessionStr=rs.getString("serverSession");
                if(StringUtils.isNotBlank(serverSessionStr)){
                    session.setServerSession(ServerSession.parseFrom(MD5Utils.hexStrToBytes(serverSessionStr)));
                }
                session.setClientId(clientId);
                return session;
            }
        }catch (Exception e){
            if(Objects.nonNull(rs)){
                try {
                    rs.close();
                } catch (SQLException innere) {
                    logger.error(innere.getMessage(),innere);
                }
            }
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 清除遗嘱
     */
    public void clearWill(Session session){
        try {
            ServerSession.Builder builder = ServerSession.newBuilder(session.getServerSession());
            builder.setWillTopic(null);
            builder.setWillMessage(null);
            session.setServerSession(builder.build());
            saveOrUpdateSession(session);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }

}
