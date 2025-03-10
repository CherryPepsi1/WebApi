package com.github.webapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Model class for User request objects.
 */
public class UserRequest {

    @NotNull(message = "Age cannot be null")
    private String name;

    @NotNull(message = "Age cannot be null")
    @PositiveOrZero(message = "Age cannot be less than 0")
    private Integer age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age.intValue();
    }

}
