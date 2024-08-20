package com.bookApi.controller;

import com.bookApi.entity.User;
import com.bookApi.entity.VerificationToken;
import com.bookApi.repository.UserRepository;
import com.bookApi.repository.VerificationTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/auth")
public class VerificationController {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    public VerificationController(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);

        if (verificationToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token invalide");
        }

        VerificationToken tokenEntity = verificationToken.get();
        
        if (tokenEntity.isExpired()) {
            return ResponseEntity.badRequest().body("Token expiré");
        }

        User user = tokenEntity.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(tokenEntity);

        return ResponseEntity.ok("Compte vérifié avec succès !");
    }
}

