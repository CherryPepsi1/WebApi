package com.github.webapi.controller;

import com.github.webapi.entity.User;
import com.github.webapi.exception.UserNotFoundException;
import com.github.webapi.model.UserRequest;
import com.github.webapi.exception.UserNotFoundException;
import com.github.webapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for handling User requests.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private static final int MAX_USERS = 10;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) Integer page) {
        int offset = 0;
        if (page != null) {
            if (page.intValue() <= 0) {
                return ResponseEntity.badRequest().build();
            } else {
                offset = (page - 1) * MAX_USERS;
            }
        }

        List<User> users = userService.getUsers(offset, MAX_USERS);
        if (users == null) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest request) {
        User user = new User(request);
        User newUser = userService.createUser(user);
        URI uri = URI.create("/users/" + newUser.getId());
        return ResponseEntity.created(uri).body(newUser);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody UserRequest request) {
        User user = new User(request);
        try {
            User updatedUser = userService.updateUserById(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
