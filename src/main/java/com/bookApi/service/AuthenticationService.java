package com.bookApi.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookApi.DTO.LoginUserDTO;
import com.bookApi.DTO.RegisterUserDTO;
import com.bookApi.DTO.UserResponseDTO;
import com.bookApi.entity.User;
import com.bookApi.entity.VerificationToken;
import com.bookApi.repository.UserRepository;
import com.bookApi.repository.VerificationTokenRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Date;
import java.util.UUID;

import java.util.logging.Logger;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService; // Add EmailService as a dependency

    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    public AuthenticationService(
        UserRepository userRepository,
        VerificationTokenRepository verificationTokenRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        EmailService emailService // Inject EmailService through the constructor
    ) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService; 
    }

    public UserResponseDTO signup(RegisterUserDTO input) {
        try {
            // Create a new user from the registration data
            User user = new User();
            user.setFirstname(input.getFirstname());
            user.setLastname(input.getLastname());
            user.setEmail(input.getEmail());
            user.setPassword(passwordEncoder.encode(input.getPassword()));
            user.setRole("ROLE_USER");
            user.setEnabled(false); 

            // Save the user in the database
            User savedUser = userRepository.save(user);

            // Generate a verification token
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, savedUser, new Date(System.currentTimeMillis() + 86400000)); // 24 hours expiration
            verificationTokenRepository.save(verificationToken);

            // Construct the verification link
            String verificationLink = "http://localhost:8084/api/auth/verify?token=" + token;

            // Send the verification email via the injected EmailService
            emailService.sendEmail(
                savedUser.getEmail(), 
                "Verification Email", 
                "Please verify your account using this link: " + verificationLink
            );

            // Generate a JWT token for the user
            String jwtToken = jwtService.generateToken(savedUser);

            // Return a UserResponseDTO object with the JWT token
            return new UserResponseDTO(
                savedUser.getFirstname(),
                savedUser.getLastname(),
                savedUser.getEmail(),
                savedUser.getRole(),
                jwtToken
            );
        } catch (Exception e) {
            logger.severe("Error during user signup: " + e.getMessage());
            throw new RuntimeException("Signup failed: " + e.getMessage());
        }
    }

    public UserResponseDTO authenticate(LoginUserDTO input) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    input.getEmail(),
                    input.getPassword()
                )
            );

            // Load the user from the database
            User user = userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isEnabled()) {
                throw new RuntimeException("Account not verified");
            }

            // Generate a token for the user
            String token = jwtService.generateToken(user);

            // Return a UserResponseDTO object
            return new UserResponseDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                token
            );
        } catch (Exception e) {
            logger.severe("Error during user authentication: " + e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}


