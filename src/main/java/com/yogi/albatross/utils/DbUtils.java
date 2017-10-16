package com.yogi.albatross.utils;

import com.google.common.collect.Lists;
import com.yogi.albatross.db.DbPool;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class DbUtils {
    public static ResultSet select(String sql, Object... params) throws Exception{
        return getStatement(sql,params).executeQuery();
    }

    public static List<Integer> insert(String sql, Object... params) throws Exception{
        PreparedStatement statement = getStatement(sql, params);
        if(statement.execute()){
            ResultSet keys = statement.getGeneratedKeys();
            List<Integer> ids= Lists.newArrayList();
            while (keys!=null && keys.next()){
                ids.add(keys.getInt(NumberUtils.INTEGER_ONE));
            }
            return ids;
        }
        return Lists.newArrayListWithCapacity(1);
    }

    public static Integer update(String sql,Object ...params) throws Exception{
        PreparedStatement statement=getStatement(sql,params);
        return statement.executeUpdate();
    }

    private static PreparedStatement getStatement(String sql,Object... params) throws Exception{
        Connection connection = DbPool.getConnect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if(params!=null && params.length>0){
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i,params[i]);
            }
        }
        return preparedStatement;
    }

}
