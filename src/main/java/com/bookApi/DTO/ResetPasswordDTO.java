package com.bookApi.DTO;

public class ResetPasswordDTO {
	
    private String newPassword;
    private String token;

    // Getters et setters

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
