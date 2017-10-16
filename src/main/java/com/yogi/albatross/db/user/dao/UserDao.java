package com.yogi.albatross.db.user.dao;

import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.db.user.dto.UserDto;
import com.yogi.albatross.utils.DbUtils;

import java.sql.ResultSet;

@Dao
public class UserDao {
    private static String SELECT_BY_USERNAME="select * from user where username='%s' limit 1";

    public UserDto selectByUsername(String username){
        try{
            ResultSet resultSet=DbUtils.select(String.format(SELECT_BY_USERNAME,username));
            if(resultSet!=null){
                while (resultSet.next()){
                    UserDto dto=new UserDto();
                    dto.setId(resultSet.getInt("id"));
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
}
