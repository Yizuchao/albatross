package com.yogi.albatross.db;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

public class DbPool {
    private static final Logger logger=LoggerFactory.getLogger(DbPool.class);
    private static DataSource dataSource;
    private static final String DB_CONFIG="db.properties";

    public static void init(){
        Properties properties=new Properties();
        try {
            properties.load(DbPool.class.getClassLoader().getResourceAsStream(DB_CONFIG));
            dataSource= DruidDataSourceFactory.createDataSource(properties);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }
    public static Connection getConnect(){
        try{
            return dataSource.getConnection();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }


}
