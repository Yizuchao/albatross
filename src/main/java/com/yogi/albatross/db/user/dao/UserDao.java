package com.yogi.albatross.db.user.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.db.user.dto.UserDto;
import com.yogi.albatross.utils.CollectionUtils;
import com.yogi.albatross.utils.DbUtils;
import com.yogi.albatross.utils.MD5Utils;
import org.apache.commons.lang3.math.NumberUtils;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Dao
public class UserDao {
    private static final String SELECT_BY_USERNAME="select * from user where username='%s' limit 1";
    private static final String INSERT="insert into user(username,password) values('%s','%s')";
    private static final String UPDATE_LAST_LOGIN_TIME="update user set lastLoginTime=? where username='%s'";

    public UserDto selectByUsername(String username){
        try{
            ResultSet resultSet=DbUtils.select(String.format(SELECT_BY_USERNAME,username));
            if(resultSet!=null){
                while (resultSet.next()){
                    UserDto dto=new UserDto();
                    dto.setId(resultSet.getLong("id"));
                    dto.setPassword(resultSet.getString("password"));
                    dto.setUsername(resultSet.getString("username"));
                    return dto;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Integer insert(String username,String password){
        try{
            List<Integer> ids = DbUtils.insert(String.format(INSERT, username, MD5Utils.encode(password)));
            if(!CollectionUtils.isEmpty(ids)){
                return ids.get(NumberUtils.INTEGER_ZERO);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return NumberUtils.INTEGER_ZERO;
    }
    public Integer updateLastLoginTime(String username){
        try{
            return DbUtils.update(String.format(UPDATE_LAST_LOGIN_TIME,username),new Date());
        }catch (Exception e){
            e.printStackTrace();
        }
        return NumberUtils.INTEGER_ZERO;
    }
}
