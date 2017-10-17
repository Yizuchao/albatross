package com.yogi.albatross.db;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

public class DbPool {
    private static DataSource dataSource;
    private static final String DB_CONFIG="db.properties";

    public static void init(){
        Properties properties=new Properties();
        try {
            properties.load(DbPool.class.getClassLoader().getResourceAsStream(DB_CONFIG));
            dataSource= DruidDataSourceFactory.createDataSource(properties);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Connection getConnect(){
        try{
            return dataSource.getConnection();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
