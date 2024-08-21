package com.bookApi.authentication.DTO;

public class UserResponseDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private String token;
    private String refreshToken;

    public UserResponseDTO(String firstname, String lastname, String email, String role, String token, String refreshToken) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.token = token;
        this.refreshToken = refreshToken;
    }
    
    

    // Getters et setters

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



	public String getRefreshToken() {
		return refreshToken;
	}



	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
