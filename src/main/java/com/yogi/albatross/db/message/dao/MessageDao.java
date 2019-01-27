package com.yogi.albatross.db.message.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.db.message.entity.Message;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.DbUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Dao
public class MessageDao {
    private static final Logger logger=LoggerFactory.getLogger(MessageDao.class);
    private static final String INSERT_SQL="insert into message (content,sended) values (?,?)";
    public Long save(Message message){
        try{
            List<Long> ids = DbUtils.insert(INSERT_SQL, message.getContent(), message.getSended().getCode());
            if(!CollectionUtils.isEmpty(ids)){
                return ids.get(0);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return NumberUtils.LONG_ZERO;
    }


}
