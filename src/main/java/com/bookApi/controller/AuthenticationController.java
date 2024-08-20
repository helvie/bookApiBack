package com.bookApi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookApi.DTO.LoginUserDTO;
import com.bookApi.DTO.RegisterUserDTO;
import com.bookApi.DTO.UserResponseDTO;
import com.bookApi.service.AuthenticationService;

@RestController
@RequestMapping("/api/user")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterUserDTO registerUserDto) {
        UserResponseDTO registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> authenticate(@RequestBody LoginUserDTO loginUserDTO) {
        UserResponseDTO authenticatedUser = authenticationService.authenticate(loginUserDTO);
        return ResponseEntity.ok(authenticatedUser);
    }
}
