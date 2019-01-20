package com.yogi.albatross.db.message.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.db.message.entity.Message;
import com.yogi.albatross.utils.DbUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dao
public class MessageDao {
    private static final Logger logger=LoggerFactory.getLogger(MessageDao.class);
    private static final String INSERT_SQL="insert into message (content,sended) values (?,?)";
    public Integer save(Message message){
        try{
            DbUtils.insert(INSERT_SQL,message.getContent(),message.getSended().getCode());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return NumberUtils.INTEGER_ZERO;
    }
}
