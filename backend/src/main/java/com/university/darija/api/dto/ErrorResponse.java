package com.university.darija.api.dto;

public class ErrorResponse {
    private boolean success;
    private String error;
    private String details;

    public ErrorResponse() {
    }

    public ErrorResponse(boolean success, String error, String details) {
        this.success = success;
        this.error = error;
        this.details = details;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
