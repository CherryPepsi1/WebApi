package com.github.webapi.controller;

import com.github.webapi.entity.User;
import com.github.webapi.exception.NotFoundException;
import com.github.webapi.model.ErrorResponse;
import com.github.webapi.model.UpsertUserRequest;
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
    public ResponseEntity<User> createUser(@Valid @RequestBody UpsertUserRequest request) {
        try {
            User user = userService.createUser(new User(request));
            URI uri = URI.create(String.join("/", this.request.getRequestURI(), Integer.toString(newUser.getId())));
            return ResponseEntity.created(uri).body(user);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @Valid @RequestBody UpsertUserRequest request) {
        try {
            User user = userService.updateUserById(id, new User(request));
            return ResponseEntity.ok(user);
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
