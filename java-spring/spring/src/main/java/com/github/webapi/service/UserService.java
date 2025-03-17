package com.github.webapi.service;

import com.github.webapi.entity.User;
import com.github.webapi.exception.NullUsersException;
import com.github.webapi.exception.UserNotFoundException;
import com.github.webapi.repository.UserRepository;
import com.github.webapi.service.DataCallbackInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing User entities.
 */
@Service
public class UserService {

    private static final int MAX_USERS = 10;

    private final UserRepository userRepository;
    private final DataService dataService;

    @Component
    public class DataCallback implements DataCallbackInterface {

        @Autowired
        public DataCallback() {
        }

        public List<User> parseResultSet(ResultSet resultSet) {
            List<User> users = new ArrayList<User>();
            try {
                while (resultSet.next()) {
                    User user = new User(
                        resultSet.getInt(UserRepository.COLUMN_ID),
                        resultSet.getString(UserRepository.COLUMN_NAME),
                        resultSet.getInt(UserRepository.COLUMN_AGE));
                    users.add(user);
                }
            } catch (Exception e) {
                System.out.println("Failed to parse result set: " + e.getMessage());
            }

            return users;
        }

    }

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.dataService = new DataService(new DataCallback());
    }

    public List<User> getUsers(Integer page) throws NullUsersException {
        List<User> users = null;
        if (page != null) {
            int offset = (page.intValue() - 1) * MAX_USERS;
            users = dataService.select(
                UserRepository.TABLE_USERS,
                MAX_USERS,
                offset,
                UserRepository.COLUMN_ID,
                UserRepository.COLUMN_NAME,
                UserRepository.COLUMN_AGE
            );
        } else {
            users = dataService.select(
                UserRepository.TABLE_USERS,
                MAX_USERS,
                UserRepository.COLUMN_ID,
                UserRepository.COLUMN_NAME,
                UserRepository.COLUMN_AGE
            );
        }

        if (users != null) {
            return users;
        }

        throw new NullUsersException();
    }

    public User getUserById(int id) throws UserNotFoundException {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        throw new UserNotFoundException();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUserById(int id, User user) throws UserNotFoundException {
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

}
