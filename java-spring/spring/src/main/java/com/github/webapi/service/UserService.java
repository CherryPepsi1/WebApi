package com.github.webapi.service;

import com.github.webapi.entity.User;
import com.github.webapi.exception.UserNotFoundException;
import com.github.webapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.System;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing User entities.
 */
@Service
public class UserService {

    private static final int TIMEOUT_SECS = 60;

    private final UserRepository userRepository;
    private Connection connection;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    }

    public List<User> getUsers(int offset, int count) {
        try {
            if (connection == null) {
                System.out.println("Database connection is null");
            } else if (!connection.isValid(TIMEOUT_SECS)) {
                System.out.println("Database connection is not valid");
            } else {
                String sql = String.format("""
                    SELECT %s, %s, %s
                    FROM %s
                    OFFSET %d ROWS
                    FETCH NEXT %d ROWS ONLY""",
                    UserRepository.COLUMN_ID,
                    UserRepository.COLUMN_NAME,
                    UserRepository.COLUMN_AGE,
                    UserRepository.TABLE_USERS,
                    offset,
                    count
                );
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                List<User> users = new ArrayList<User>();
                while (resultSet.next()) {
                    User user = new User(
                        resultSet.getInt(UserRepository.COLUMN_ID),
                        resultSet.getString(UserRepository.COLUMN_NAME),
                        resultSet.getInt(UserRepository.COLUMN_AGE));
                    users.add(user);
                }

                return users;
            }

        } catch (SQLException e) {
            System.out.println("Failed to execute query: " + e.getMessage());
        }

        return null;
    }

    public User getUserById(int id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        throw new UserNotFoundException();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserById(int id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.copyFrom(user);
            return userRepository.save(updatedUser);
        }

        throw new UserNotFoundException();
    }

    public void deleteUserById(int id) {
        userRepository.deleteById(id);
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
