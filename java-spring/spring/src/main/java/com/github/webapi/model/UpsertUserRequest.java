package com.github.webapi.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Model class for create or update User request objects.
 */
public class UpsertUserRequest {

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Age cannot be null")
    @PositiveOrZero(message = "Age must be greater than or equal to 0")
    private Integer age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age.intValue();
    }

}
