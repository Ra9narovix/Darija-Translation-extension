package com.university.darija.api.dto;

public class HealthResponse {
    private boolean success;
    private String status;
    private String provider;
    private String model;

    public HealthResponse() {
    }

    public HealthResponse(boolean success, String status, String provider, String model) {
        this.success = success;
        this.status = status;
        this.provider = provider;
        this.model = model;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
