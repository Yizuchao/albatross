package com.yogi.albatross.utils;

import com.google.common.collect.Lists;
import com.yogi.albatross.db.DbPool;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DbUtils {
    private static final Logger logger=LoggerFactory.getLogger(DbUtils.class);
    public static ResultSet select(String sql, Object... params) throws Exception {
        return getStatement(sql, params).executeQuery();
    }

    public static List<Integer> insert(String sql, Object... params) throws Exception {
        PreparedStatement statement = getStatement(sql, true, params);
        if (Objects.nonNull(statement) && statement.executeUpdate() > 0) {
            ResultSet keys = statement.getGeneratedKeys();
            List<Integer> ids = Lists.newArrayList();
            while (keys != null && keys.next()) {
                ids.add(keys.getInt(NumberUtils.INTEGER_ONE));
            }
            return ids;
        }
        return Lists.newArrayListWithCapacity(1);
    }

    public static Integer update(String sql, Object... params) throws Exception {
        PreparedStatement statement = getStatement(sql, params);
        return Objects.nonNull(statement) ? statement.executeUpdate() : null;
    }

    private static PreparedStatement getStatement(String sql, Object... params) throws Exception {
        return getStatement(sql, false, params);
    }

    private static PreparedStatement getStatement(String sql, boolean isInsert, Object... params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DbPool.getConnect();
            if (isInsert) {
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement(sql);
            }

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    preparedStatement.setObject(i + NumberUtils.INTEGER_ONE, params[i]);
                }
            }
            return preparedStatement;
        } catch (Exception e) {
            try {
                if (Objects.nonNull(connection)) {
                    connection.close();
                }
                if (Objects.nonNull(preparedStatement)) {
                    preparedStatement.close();
                }
            } catch (Exception e1) {
                logger.error(e1.getMessage(),e1);
            }
            logger.error(e.getMessage(),e);
        }
        return null;
    }

}
