package com.spring.jdbc;

import com.spring.AppContext;
import com.spring.ioc.annotation.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class JdbcTemplate {

    public <T> Integer save(String sql, ObjectMapper<T> mapper, T data) throws SQLException, ClassNotFoundException {
        Connection connection = (Connection) AppContext.getContext().getObject(AppContext.CONNECTION);
        if (connection == null) {
            connection = DBUtil.getConnection();
            AppContext.getContext().setObject(AppContext.CONNECTION, connection);
        }

        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        mapper.mapperParam(statement, data);
        statement.executeUpdate();
        ResultSet generatedKeys = statement.getGeneratedKeys();
        Integer id = null;
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(1);
        }
        DBUtil.close(null, statement, generatedKeys);
        return id;
    }

    public <T> boolean execute(String sql, ObjectMapper<T> mapper, T data) throws SQLException, ClassNotFoundException {
        Connection connection = (Connection) AppContext.getContext().getObject(AppContext.CONNECTION);
        if (connection == null) {
            connection = DBUtil.getConnection();
            AppContext.getContext().setObject(AppContext.CONNECTION, connection);
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        mapper.mapperParam(statement, data);
        int i = statement.executeUpdate();
        DBUtil.close(null, statement, null);
        return i == 1;
    }

    // save update delete
    public boolean execute(String sql, Object... objects) throws SQLException, ClassNotFoundException {
        Connection connection = (Connection) AppContext.getContext().getObject(AppContext.CONNECTION);
        if (connection == null) {
            connection = DBUtil.getConnection();
            AppContext.getContext().setObject(AppContext.CONNECTION, connection);
        }
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < objects.length; i++) {
            statement.setObject(i + 1, objects[i]);
        }
        int i = statement.executeUpdate();
        DBUtil.close(null, statement, null);
        return i == 1;
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... objects) throws Exception {
        Connection connection = (Connection) AppContext.getContext().getObject(AppContext.CONNECTION);
        if (connection == null) {
            connection = DBUtil.getConnection();
            AppContext.getContext().setObject(AppContext.CONNECTION, connection);
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < objects.length; i++) {
            statement.setObject(i + 1, objects[i]);
        }
        ResultSet resultSet = statement.executeQuery();
        T t = null;
        if (resultSet.next()) {
            t = rowMapper.mapRow(resultSet, 0);
        }
        DBUtil.close(null, statement, resultSet);
        return t;
    }

    public <T> T queryForObject(String sql, Object... objects) throws SQLException, ClassNotFoundException {
        Connection connection = (Connection) AppContext.getContext().getObject(AppContext.CONNECTION);
        if (connection == null) {
            connection = DBUtil.getConnection();
            AppContext.getContext().setObject(AppContext.CONNECTION, connection);
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        if (objects.length > 0) {
            for (int i = 0; i < objects.length; i++) {
                statement.setObject(i + 1, objects[i]);
            }
        }
        ResultSet resultSet = statement.executeQuery();
        T t = null;
        if (resultSet.next()) {
            t = (T) resultSet.getObject(1);
        }
        DBUtil.close(null, statement, resultSet);
        return t;
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... objects) throws Exception {
        Connection connection = (Connection) AppContext.getContext().getObject(AppContext.CONNECTION);
        if (connection == null) {
            connection = DBUtil.getConnection();
            AppContext.getContext().setObject(AppContext.CONNECTION, connection);
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < objects.length; i++) {
            statement.setObject(i + 1, objects[i]);
        }
        ResultSet resultSet = statement.executeQuery();
        List<T> list = new ArrayList<T>();
        for (int i = 0; resultSet.next(); i++) {
            T t = rowMapper.mapRow(resultSet, i);
            list.add(t);
        }
        DBUtil.close(null, statement, resultSet);
        return list;
    }

}
