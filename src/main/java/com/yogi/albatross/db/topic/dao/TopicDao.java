package com.yogi.albatross.db.topic.dao;

import com.alibaba.druid.sql.SQLUtils;
import com.google.common.collect.Lists;
import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.constants.common.SubscribeQos;
import com.yogi.albatross.utils.DbUtils;
import com.yogi.albatross.utils.SqlUtils;

import java.sql.ResultSet;
import java.util.List;

@Dao
public class TopicDao {
    private static final String INSERT="insert into topic(name,creator) values %s";
    private static final String SELECT_BY_NAMES="select id,name,creator from topic where %s";
    private static final String SUBSCRIBE="insert into subscribe(topicName,subscriber,qos) values %s";

    public boolean saveOrSubscribe(List<String> topicNames, Integer currentUser, List<SubscribeQos> qoss){
        try{
            ResultSet resultSet = DbUtils.select(String.format(SELECT_BY_NAMES,
                    SqlUtils.getINSql("name",topicNames)));
            List<String> subscribedTopic= Lists.newArrayList();
            while (resultSet!=null && resultSet.next()){
                subscribedTopic.add(resultSet.getString("name"));
            }

            //insert topic
            int size=topicNames.size();
            int subscribedSize=subscribedTopic.size();
            List<String> newInsert=Lists.newArrayList();
            List<SubscribeQos> newQos=Lists.newArrayList();
            if(size>subscribedSize){
                StringBuilder topicSb=new StringBuilder();
                for (int i = 0; i < size; i++) {
                    String topicName=topicNames.get(i);
                    if(!subscribedTopic.contains(topicName)){
                        newInsert.add(topicName);
                        newQos.add(qoss.get(i));
                        topicSb.append(SqlUtils.LEFT_CLOSE);
                        topicSb.append(SqlUtils.CHAR_HOLDER).append(topicNames.get(i)).append(SqlUtils.CHAR_HOLDER);
                        topicSb.append(SqlUtils.CHAR_HOLDER).append(currentUser).append(SqlUtils.CHAR_HOLDER);
                        topicSb.append(SqlUtils.RIGHT_CLOSE).append(SqlUtils.SEPARATOR);
                    }
                }
                DbUtils.insert(String.format(INSERT,topicSb.toString()));
            }

            //subscribe
            int newInsertSize=newInsert.size();
            if(newInsertSize>0){
                StringBuilder newSb=new StringBuilder();
                for (int i = 0; i < newInsertSize; i++) {
                    newSb.append(SqlUtils.LEFT_CLOSE);
                    newSb.append(SqlUtils.CHAR_HOLDER).append(topicNames.get(i)).append(SqlUtils.CHAR_HOLDER);
                    newSb.append(SqlUtils.CHAR_HOLDER).append(currentUser).append(SqlUtils.CHAR_HOLDER);
                    newSb.append(SqlUtils.CHAR_HOLDER).append(newQos.get(i).getCode()).append(SqlUtils.CHAR_HOLDER);
                    newSb.append(SqlUtils.RIGHT_CLOSE).append(SqlUtils.SEPARATOR);
                }
                DbUtils.insert(String.format(SUBSCRIBE,newSb.toString()));
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
