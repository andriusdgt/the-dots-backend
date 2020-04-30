package com.andriusdgt.thedots.backend.model;

public final class ApiError {

    private final String message;

    public ApiError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
