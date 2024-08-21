package com.bookApi.authentication.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterUserDTO {
    @NotBlank(message = "L'adresse e-mail est requise")
    @Email(message = "L'adresse e-mail doit être valide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
        message = "Le mot de passe doit contenir au moins une majuscule et un caractère spécial"
    )
    private String password;

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    @Pattern(regexp = "^[A-Za-zà-ÿÀ-Ÿ'\\-\\s]+$", message = "Le prénom ne doit contenir que des lettres, des espaces, des apostrophes ou des tirets")
    private String firstname;
    
    @NotBlank(message = "Le nom de famille est requis")
    @Size(max = 50, message = "Le nom de famille ne doit pas dépasser 50 caractères")
    @Pattern(regexp = "^[A-Za-zà-ÿÀ-Ÿ'\\-\\s]+$", message = "Le nom de famille ne doit contenir que des lettres, des espaces, des apostrophes ou des tirets")
    private String lastname;

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
