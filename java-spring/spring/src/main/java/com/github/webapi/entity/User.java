package com.github.webapi.entity;

import com.github.webapi.model.UpsertUserRequest;
import com.github.webapi.repository.UserRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Entity class for User objects.
 */
@Entity
@Table(name = UserRepository.TABLE_USERS)
public class User {

    // Properties
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String name;

    private int age;

    // Constructors
    public User() {
    }

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public User(UpsertUserRequest request) {
        this.name = request.getName();
        this.age = request.getAge();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Modifiers
    public void copyFrom(User user) {
        this.name = user.name;
        this.age = user.age;
    }

}
