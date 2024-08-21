package com.bookApi.authentication.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordDTO {
	
    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
        message = "Le mot de passe doit contenir au moins une majuscule et un caractère spécial"
    )
    private String newPassword;

    @NotBlank(message = "Le jeton est requis")
    @Size(min = 32, max = 64, message = "Le jeton doit contenir entre 32 et 64 caractères")
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
