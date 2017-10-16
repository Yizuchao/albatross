package com.yogi.albatross.db;

import com.google.common.collect.Maps;
import com.yogi.albatross.annotation.Dao;
import com.yogi.albatross.utils.ClassUtils;
import com.yogi.albatross.utils.CollectionUtils;

import java.util.List;
import java.util.Map;

public class DaoManager {
    private static final Map<Class<?>,Object> map= Maps.newHashMap();
    public static void init(String pkg){
        //init connectio pool
        DbPool.init();

        List<Class<?>> classList = ClassUtils.getClassList(pkg, true, Dao.class);
        if(!CollectionUtils.isEmpty(classList)){
            int size=classList.size();
            try{
                for (int i = 0; i < size; i++) {
                    Class<?> clazz=classList.get(i);
                    map.put(clazz,clazz.newInstance());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    public static <T> T getDao(Class<T> key){
        return (T)map.get(key);
    }

}
