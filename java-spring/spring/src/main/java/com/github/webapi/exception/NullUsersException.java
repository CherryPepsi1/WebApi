package com.github.webapi.exception;

public class NullUsersException extends RuntimeException {

    public NullUsersException() {
        super("Users list is null");
    }

}
