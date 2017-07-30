package com.spring.jdbc;

import com.spring.util.PropertyUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(PropertyUtils.getProperty("jdbc.driver"));
        Connection connection = DriverManager.getConnection(PropertyUtils.getProperty("jdbc.url"),
                PropertyUtils.getProperty("jdbc.user"), PropertyUtils.getProperty("jdbc.password"));

        return connection;
    }

    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

}
