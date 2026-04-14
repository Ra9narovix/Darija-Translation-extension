package com.university.darija.exception;

public class AppException extends RuntimeException {
    private final int status;
    private final String userMessage;

    public AppException(int status, String userMessage, String details) {
        super(details);
        this.status = status;
        this.userMessage = userMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
