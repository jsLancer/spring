package com.spring.jdbc;

import java.sql.ResultSet;

public interface RowMapper<T> {

    T mapRow(ResultSet resultSet, int i) throws Exception;
}
