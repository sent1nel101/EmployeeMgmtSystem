package com.dmcdesigns.capstone.DTOs;

public class PasswordResetResponse {
    private String message;
    private String resetToken;

    public PasswordResetResponse() {}

    public PasswordResetResponse(String message, String resetToken) {
        this.message = message;
        this.resetToken = resetToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
