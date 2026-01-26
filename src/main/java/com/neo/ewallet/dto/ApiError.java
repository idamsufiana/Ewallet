package com.neo.ewallet.dto;

import java.time.Instant;

public class ApiError {

    private String status;
    private String message;
    private Instant timestamp;

    private ApiError(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public static ApiError badRequest(String message) {
        return new ApiError("error", message);
    }

    public static ApiError internalError(String message) {
        return new ApiError("error", message);
    }
}
