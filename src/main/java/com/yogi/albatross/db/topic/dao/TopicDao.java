package com.yogi.albatross.db.topic.dao;

import com.google.common.collect.Lists;
import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.constants.common.SubscribeQos;
import com.yogi.albatross.db.topic.dto.SubscribeDto;
import com.yogi.albatross.utils.DbUtils;
import com.yogi.albatross.utils.SqlUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.ResultSet;
import java.util.List;

@Dao
public class TopicDao {
    private static final String INSERT = "insert into topic(name,creator) values %s";
    private static final String SELECT_BY_NAMES = "select id,name,creator from topic where %s";
    private static final String SUBSCRIBE = "insert into subscribe(topicName,subscriber,qos) values %s";
    private static final String NEWEST_100 = "select topicName,subscriber,qos from subscribe where subscriber=?";

    /**
     * 保存主题。返回保存失败的主题
     *
     * @param topicNames
     * @param currentUser
     * @param qoss
     * @return
     */
    public boolean saveOrSubscribe(List<String> topicNames, Long currentUser, List<SubscribeQos> qoss) {
        List<String> exsitsTopic = Lists.newArrayListWithCapacity(NumberUtils.INTEGER_ONE);
        try {
            ResultSet resultSet = DbUtils.select(String.format(SELECT_BY_NAMES,
                    SqlUtils.getINSql("name", topicNames)));
            while (resultSet != null && resultSet.next()) {
                exsitsTopic.add(resultSet.getString("name"));
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
                        topicSb.append(SqlUtils.CHAR_HOLDER).append(currentUser).append(SqlUtils.CHAR_HOLDER);
                        topicSb.append(SqlUtils.RIGHT_CLOSE);
                        if (i != size - 1) {
                            topicSb.append(SqlUtils.SEPARATOR);
                        }
                    }
                }
                DbUtils.insert(String.format(INSERT, topicSb.toString()));
            }

            //subscribe
            StringBuilder newSb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                newSb.append(SqlUtils.LEFT_CLOSE);
                newSb.append(SqlUtils.CHAR_HOLDER).append(topicNames.get(i)).append(SqlUtils.CHAR_HOLDER);
                newSb.append(SqlUtils.CHAR_HOLDER).append(currentUser).append(SqlUtils.CHAR_HOLDER);
                newSb.append(SqlUtils.CHAR_HOLDER).append(qoss.get(i).getCode()).append(SqlUtils.CHAR_HOLDER);
                newSb.append(SqlUtils.RIGHT_CLOSE);
                if (i == size - 1) {
                    newSb.append(SqlUtils.SEPARATOR);
                }
            }
            DbUtils.insert(String.format(SUBSCRIBE, newSb.toString()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取最新的100个订阅主题
     *
     * @param userId
     * @return
     */
    public List<SubscribeDto> get100Newest(Long userId) {
        List<SubscribeDto> topics = Lists.newArrayListWithExpectedSize(100);
        try {
            ResultSet resultSet = DbUtils.select(NEWEST_100, userId);
            while (resultSet != null && resultSet.next()) {
                SubscribeDto topic = new SubscribeDto();
                topic.setName(resultSet.getString("topicName"));
                topic.setCreator(resultSet.getLong("subscriber"));
                topic.setQos(SubscribeQos.valueOf(resultSet.getInt("qos")));
                topics.add(topic);
            }
            return topics;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topics;
    }

}
