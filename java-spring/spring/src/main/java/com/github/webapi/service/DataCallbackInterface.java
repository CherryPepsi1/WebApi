package com.github.webapi.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for managing data callbacks.
 */
interface DataCallbackInterface {

    public <T> List<T> parseResultSet(ResultSet resultSet) throws SQLException;

}
