package com.yogi.albatross.db.message.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.utils.DbUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dao
public class ClientMessageDao {
    private static final Logger logger=LoggerFactory.getLogger(ClientMessageDao.class);
    private static final String UPDATE_RETRY="update client_message set retry=? where clientId=? and messageId=?";

    public Integer updateRetry(String clientId,Long messageId,long retry){
        try{
            return DbUtils.update(UPDATE_RETRY, retry, clientId, messageId);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return NumberUtils.INTEGER_ZERO;
    }
}
