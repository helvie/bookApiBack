package com.bookApi.authentication.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RequestResetPasswordDTO {
	
    @NotBlank(message = "L'adresse e-mail est requise")
    @Email(message = "L'adresse e-mail doit être valide")
    
    private String email;
    // Constructeurs, getters et setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
