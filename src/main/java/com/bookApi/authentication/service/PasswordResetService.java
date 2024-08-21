package com.bookApi.authentication.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookApi.authentication.entity.PasswordResetToken;
import com.bookApi.authentication.repository.PasswordResetTokenRepository;
import com.bookApi.entity.User;
import com.bookApi.repository.UserRepository;
import com.bookApi.authentication.service.AuthenticationEmailService;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;  
    private final PasswordResetTokenRepository tokenRepository;  
    private final AuthenticationEmailService emailService;  
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository,
                                AuthenticationEmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;  
        this.tokenRepository = tokenRepository; 
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    //----------------------------- REQUEST PASSWORD RESET -----------------------------
    
    // @param email Adresse email de l'utilisateur.
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();  // Génère un token de réinitialisation unique

        PasswordResetToken resetToken = new PasswordResetToken(token, user, new Date(System.currentTimeMillis() + 3600000)); // Création du token avec une expiration d'une heure
        tokenRepository.save(resetToken);  

        String resetLink = "http://localhost:4200/reset-password?token=" + token;  // Génération du lien de réinitialisation
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstname(), resetLink); 
    }
    
    //------------------------------ RESET PASSWORD ------------------------------
    
    // Réinitialise le mot de passe de l'utilisateur avec un token valide.
    // @param token Token de réinitialisation.
    // @param newPassword Nouveau mot de passe.
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));  // Vérifie si le token est valide

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token expired");  // Vérifie si le token est expiré
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));  // Encode et met à jour le mot de passe de l'utilisateur
        userRepository.save(user);

        tokenRepository.delete(resetToken);  // Supprime le token après utilisation
    }
    
    //------------------------------ CHANGE PASSWORD ------------------------------
    
    // Change le mot de passe de l'utilisateur après vérification de l'ancien mot de passe.
    // @param username Nom d'utilisateur de l'utilisateur.
    // @param oldPassword Ancien mot de passe.
    // @param newPassword Nouveau mot de passe.
    // @return true si le mot de passe a été changé avec succès, sinon false.
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;  // Vérifie si l'ancien mot de passe est correct
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
