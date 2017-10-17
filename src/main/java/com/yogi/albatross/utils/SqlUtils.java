package com.yogi.albatross.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SqlUtils {
    private static final int IN_MAX=1000;
    public static final String CHAR_HOLDER ="'";
    public static final String SEPARATOR=",";
    public static final String LEFT_CLOSE="(";
    public static final String RIGHT_CLOSE=")";

    public static final String getINSql(String colName,List<?> params){
        if(CollectionUtils.isEmpty(params)){
            return StringUtils.EMPTY;
        }
        int size=params.size();
        StringBuilder sb=new StringBuilder();
        if(size<IN_MAX){
            sb.append(colName).append(" in ").append(LEFT_CLOSE);
            for (int i = 0; i < size; i++) {
                sb.append(CHAR_HOLDER).append(String.valueOf(params.get(i))).append(CHAR_HOLDER);
                if(i!=size-1){
                    sb.append(SEPARATOR);
                }
            }
            sb.append(RIGHT_CLOSE);
        }else {
            sb.append(LEFT_CLOSE);
            int groupStart=0;
            boolean modFlag=false;
            for (int i = 0; i < size; i++) {
                modFlag=(i+1)%IN_MAX==0;
                if(i==0 || modFlag){
                    if(i!=0){
                        sb.append(" and ");
                    }
                    sb.append(colName).append(" in ").append(LEFT_CLOSE);
                    groupStart=i;
                }
                sb.append(CHAR_HOLDER).append(String.valueOf(params.get(i))).append(CHAR_HOLDER);
                if(!modFlag){
                    sb.append(SEPARATOR);
                }
                if(modFlag && groupStart>i){
                    sb.append(RIGHT_CLOSE);
                }
            }
            sb.append(RIGHT_CLOSE);
        }
        return sb.toString();
    }
}
