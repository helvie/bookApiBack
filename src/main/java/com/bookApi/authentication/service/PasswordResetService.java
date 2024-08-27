package com.bookApi.authentication.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookApi.authentication.entity.PasswordResetToken;
import com.bookApi.authentication.repository.PasswordResetTokenRepository;
import com.bookApi.entity.User;
import com.bookApi.exception.InvalidTokenException;
import com.bookApi.exception.UserNotFoundException;
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
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé."));

        // Chercher un token existant pour cet utilisateur
        Optional<PasswordResetToken> existingToken = tokenRepository.findByUserAndExpiryDateAfter(user, new Date());

        PasswordResetToken resetToken;
        if (existingToken.isPresent()) {
            // Réutiliser le token existant
            resetToken = existingToken.get();
        } else {
            // Créer un nouveau token si aucun n'existe ou si le précédent est expiré
            String token = UUID.randomUUID().toString();
            resetToken = new PasswordResetToken(token, user, new Date(System.currentTimeMillis() + 3600000));
            tokenRepository.save(resetToken);
        }

        String resetLink = "http://localhost:4200/auth/reset-password?token=" + resetToken.getToken()+"&email=" + email;  // Génération du lien de réinitialisation
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstname(), resetLink); 
    }
    
    //------------------------------ RESET PASSWORD ------------------------------
    
    // Réinitialise le mot de passe de l'utilisateur avec un token valide.
    // @param token Token de réinitialisation.
    // @param newPassword Nouveau mot de passe.
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token invalide."));  // Vérifie si le token est valide

        if (resetToken.isExpired()) {
            throw new InvalidTokenException("Le token de réinitialisation a expiré.");  // Vérifie si le token est expiré
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
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;  // Vérifie si l'ancien mot de passe est correct
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
