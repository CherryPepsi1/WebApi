package com.github.webapi.controller;

import com.github.webapi.entity.User;
import com.github.webapi.exception.NotFoundException;
import com.github.webapi.model.ErrorResponse;
import com.github.webapi.model.UserRequest;
import com.github.webapi.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for handling User requests.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private HttpServletRequest request;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private ResponseEntity buildErrorResponse(HttpStatus status) {
        ErrorResponse response = new ErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI());
        return new ResponseEntity(response, status);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) @Min(1) Integer page) {
        try {
            List<User> users = userService.getUsers(page);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (NotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest request) {
        try {
            User user = new User(request);
            User newUser = userService.createUser(user);
            URI uri = URI.create("/users/" + newUser.getId());
            return ResponseEntity.created(uri).body(newUser);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @Valid @RequestBody UserRequest request) {
        try {
            User user = new User(request);
            User updatedUser = userService.updateUserById(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (NotFoundException e) {
            return buildErrorResponse(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable int id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
