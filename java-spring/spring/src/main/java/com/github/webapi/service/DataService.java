package com.github.webapi.service;

import com.github.webapi.exception.DataException;
import com.github.webapi.service.DataCallbackInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

/**
 * Service class for managing database operations.
 */
@Service
public class DataService {

    private static final int TIMEOUT_SECS = 60;
    private Connection connection;

    @Autowired
    public DataService() {
        try {
            this.connection = DriverManager.getConnection(
                System.getenv("DATABASE_URL"),
                System.getenv("DATABASE_USERNAME"),
                System.getenv("DATABASE_PASSWORD")
            );
        } catch (SQLException e) {
            System.err.println("Failed to open database connection: " + e.getMessage());
            this.connection = null;
        }
    }

    public <T> List<T> querySelect(DataCallbackInterface callback, String table, int count, Integer offset, String[] columns) throws Exception {
        if (callback == null) {
            throw new IllegalArgumentException("Data callback cannot be null");
        } else if (connection == null) {
            throw new IllegalStateException("Database connection is null");
        } else if (!connection.isValid(TIMEOUT_SECS)) {
            throw new IllegalStateException("Database connection is not valid");
        }

        try {
            StringBuilder sqlBuilder = new StringBuilder("SELECT ");
            if (columns == null || columns.length == 0) {
                sqlBuilder.append("*");
            } else {
                for (int i = 0; i < columns.length; i++) {
                    if (i > 0) {
                        sqlBuilder.append(", ");
                    }
                    sqlBuilder.append(columns[i]);
                }
            }
            sqlBuilder.append(" FROM " + table);
            sqlBuilder.append(" LIMIT " + count);
            if (offset != null) {
                sqlBuilder.append(" OFFSET " + offset.intValue());
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());
            return callback.parseResultSet(resultSet);

        } catch (SQLException e) {
            System.err.println("Failed to execute select query: " + e.getMessage());
            throw new DataException("Database operation failed", e);
        }
    }

    public <T> List<T> querySelect(DataCallbackInterface callback, String table, int count, Integer offset) throws Exception {
        return querySelect(callback, table, count, offset, null);
    }

    public <T> List<T> querySelect(DataCallbackInterface callback, String table, int count, String[] columns) throws Exception {
        return querySelect(callback, table, count, null, columns);
    }

    public <T> List<T> querySelect(DataCallbackInterface callback, String table, int count) throws Exception {
        return querySelect(callback, table, count, null, null);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        } finally {
            super.finalize();
        }
    }

}
