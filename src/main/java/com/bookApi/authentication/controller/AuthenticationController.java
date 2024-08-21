package com.bookApi.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookApi.authentication.DTO.LoginUserDTO;
import com.bookApi.authentication.DTO.RegisterUserDTO;
import com.bookApi.authentication.DTO.UserResponseDTO;
import com.bookApi.authentication.service.AuthenticationService;
import com.bookApi.authentication.service.RefreshTokenService;

@RestController
@RequestMapping("/api/user")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    // Constructeur pour initialiser les services nécessaires à l'authentification et au rafraîchissement des tokens
    public AuthenticationController(AuthenticationService authenticationService, RefreshTokenService refreshTokenService) {
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
    }

    //------------------------------------------ SIGNUP ----------------------------------------
    
    // @param registerUserDto les informations d'inscription de l'utilisateur
    // @return ResponseEntity<UserResponseDTO> contenant les détails de l'utilisateur inscrit
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterUserDTO registerUserDto) {
        UserResponseDTO registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    //------------------------------------------ LOGIN -----------------------------------------
    
    // @param loginUserDTO les informations de connexion de l'utilisateur
    // @return ResponseEntity<UserResponseDTO> contenant les détails de l'utilisateur authentifié
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginUserDTO loginUserDTO) {
        UserResponseDTO authenticatedUser = authenticationService.authenticate(loginUserDTO);
        return ResponseEntity.ok(authenticatedUser);
    }

    //-------------------------------------- REFRESH TOKEN -------------------------------------

    // @param refreshToken le token de rafraîchissement de l'utilisateur
    // @return ResponseEntity<?> contenant le nouveau token d'authentification
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }
}
