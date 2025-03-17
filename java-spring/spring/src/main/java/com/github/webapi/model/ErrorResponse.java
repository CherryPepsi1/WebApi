package com.github.webapi.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;

/**
 * Model class for error response objects.
 */
@JsonPropertyOrder({ "timestamp", "status", "error", "path" })
public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String path;

    public ErrorResponse(int status, String error, String path) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

}
