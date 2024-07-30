package com.bookApi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookApi.DTO.LoginResponseDTO;
import com.bookApi.DTO.LoginUserDTO;
import com.bookApi.DTO.RegisterUserDTO;
import com.bookApi.entity.User;
import com.bookApi.service.AuthenticationService;
import com.bookApi.service.JwtService;

@RequestMapping("/api/user")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginUserDTO loginUserDTO) {
        User authenticatedUser = authenticationService.authenticate(loginUserDTO);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(jwtToken);
        loginResponseDTO.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponseDTO);
    }
}
