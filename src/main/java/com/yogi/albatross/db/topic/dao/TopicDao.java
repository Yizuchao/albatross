package com.yogi.albatross.db.topic.dao;

import com.google.common.collect.Lists;
import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.constants.common.SubscribeQos;
import com.yogi.albatross.db.topic.dto.Subscribe;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.DbUtils;
import com.yogi.albatross.utils.SqlUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Dao
public class TopicDao {
    private static final Logger logger=LoggerFactory.getLogger(TopicDao.class);
    private static final String INSERT_TOPIC = "insert into topic(name,creator) values %s";
    private static final String SELECT_BY_NAMES = "select id,name,creator from topic where %s";
    private static final String INSERT_SUBSCRIBE = "insert into subscribe(topicName,subscriber,qos) values %s";
    private static final String SELECT_SUBSCRIBE="select id,topicName from subscribe where %s and subscriber=?";

    /**
     * 保存主题。返回保存失败的主题
     *
     * @param topicNames
     * @param clientId
     * @param qoss
     * @return
     */
    public boolean saveOrSubscribe(List<String> topicNames, String clientId, List<SubscribeQos> qoss) {
        ResultSet topicResultSet =null;
        ResultSet subscribeResultSet=null;
        try {
            List<String> exsitsTopic = Lists.newArrayListWithCapacity(NumberUtils.INTEGER_ONE);
            topicResultSet=DbUtils.select(String.format(SELECT_BY_NAMES,
                    SqlUtils.getINSql("name", topicNames)));
            while (topicResultSet != null && topicResultSet.next()) {
                exsitsTopic.add(topicResultSet.getString("name"));
            }

            //insert topic
            int size = topicNames.size();
            int subscribedSize = exsitsTopic.size();
            if (size > subscribedSize) {
                StringBuilder topicSb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    String topicName = topicNames.get(i);
                    if (!exsitsTopic.contains(topicName)) {
                        topicSb.append(SqlUtils.LEFT_CLOSE);
                        topicSb.append(SqlUtils.CHAR_HOLDER).append(topicNames.get(i)).append(SqlUtils.CHAR_HOLDER);
                        topicSb.append(SqlUtils.CHAR_HOLDER).append(clientId).append(SqlUtils.CHAR_HOLDER);
                        topicSb.append(SqlUtils.RIGHT_CLOSE);
                        if (i != size - 1) {
                            topicSb.append(SqlUtils.SEPARATOR);
                        }
                    }
                }
                DbUtils.insert(String.format(INSERT_TOPIC, topicSb.toString()));
            }

            //subscribe
            List<String> existSubscribes =Lists.newArrayList();
            subscribeResultSet = DbUtils.select(String.format(SELECT_SUBSCRIBE,SqlUtils.getINSql("topicName", topicNames)), clientId);
            while (Objects.nonNull(subscribeResultSet) && subscribeResultSet.next()){
                existSubscribes.add(subscribeResultSet.getString("topicName"));
            }

            //inert into not exists subcribe
            List<String> notExistSubcribe=topicNames.stream().filter(topicName->!existSubscribes.contains(topicName)).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(notExistSubcribe)){
                StringBuilder newSb = new StringBuilder();
                for (int i = 0; i < notExistSubcribe.size(); i++) {
                    if (i >0) {
                        newSb.append(SqlUtils.SEPARATOR);
                    }
                    newSb.append(SqlUtils.LEFT_CLOSE);
                    newSb.append(SqlUtils.CHAR_HOLDER).append(notExistSubcribe.get(i)).append(SqlUtils.CHAR_HOLDER).append(SqlUtils.SEPARATOR);
                    newSb.append(SqlUtils.CHAR_HOLDER).append(clientId).append(SqlUtils.CHAR_HOLDER).append(SqlUtils.SEPARATOR);
                    newSb.append(SqlUtils.CHAR_HOLDER).append(qoss.get(i).getCode()).append(SqlUtils.CHAR_HOLDER);
                    newSb.append(SqlUtils.RIGHT_CLOSE);
                }
                DbUtils.insert(String.format(INSERT_SUBSCRIBE, newSb.toString()));
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }finally {
            if(Objects.nonNull(topicResultSet)){
                try {
                    topicResultSet.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
        return false;
    }
}
