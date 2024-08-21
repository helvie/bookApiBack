package com.bookApi.authentication.controller;

import com.bookApi.authentication.entity.VerificationToken;
import com.bookApi.authentication.repository.VerificationTokenRepository;
import com.bookApi.entity.User;
import com.bookApi.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class VerificationController {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    public VerificationController(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    //------------------------ VÉRIFICATION DU COMPTE ------------------------

    // Vérification du compte utilisateur en utilisant le token de vérification.
    // @param token le token de vérification
    // @return ResponseEntity avec le résultat de la vérification

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
    	
        // Recherche du token
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);

        if (verificationToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token invalide");
        }

        VerificationToken tokenEntity = verificationToken.get();
        
        if (tokenEntity.isExpired()) {
            return ResponseEntity.badRequest().body("Token expiré");
        }

        // Activation de l'utilisateur
        User user = tokenEntity.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // Suppression du token
        tokenRepository.delete(tokenEntity);

        return ResponseEntity.ok("Compte vérifié avec succès !");
    }
}


