package com.github.webapi.service;

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
    private DataCallbackInterface callback;

    @Autowired
    public DataService(DataCallbackInterface callback) {
        try {
            this.connection = DriverManager.getConnection(
                System.getenv("DATABASE_URL"),
                System.getenv("DATABASE_USERNAME"),
                System.getenv("DATABASE_PASSWORD")
            );
        } catch (SQLException e) {
            System.out.println("Failed to open database connection: " + e.getMessage());
            this.connection = null;
        }

        this.callback = callback;
    }

    private boolean isValid() {
        try {
            if (connection == null) {
                System.out.println("Database connection is null");
            } else if (!connection.isValid(TIMEOUT_SECS)) {
                System.out.println("Database connection is not valid");
            } else if (callback == null) {
                System.out.println("Data callback is null");
            } else {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to validate connection: " + e.getMessage());
        }

        return false;
    }

    public <T> List<T> select(String table, int count, Integer offset, String... columns) {
        try {
            if (isValid()) {
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
                    sqlBuilder.append(" OFFSET " + offset);
                }

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());
                return callback.parseResultSet(resultSet);
            }

        } catch (SQLException e) {
            System.out.println("Failed to execute select query: " + e.getMessage());
        }

        return null;
    }

    public <T> List<T> select(String table, int count, String... columns) {
        return select(table, count, null, columns);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Failed to close database connection: " + e.getMessage());
        } finally {
            super.finalize();
        }
    }

}
