package com.spring.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ObjectMapper<T> {

    void mapperParam(PreparedStatement statement, T t) throws SQLException;

}