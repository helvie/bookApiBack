package com.bookApi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import com.bookApi.DTO.ChangePasswordRequestDTO;
import com.bookApi.DTO.RequestResetPasswordDTO;
import com.bookApi.DTO.ResetPasswordDTO;
import com.bookApi.service.PasswordResetService;

import java.util.logging.Logger;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private static final Logger logger = Logger.getLogger(PasswordResetController.class.getName());

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody RequestResetPasswordDTO request) {
        String email = request.getEmail();  // Récupérer l'email à partir de l'objet request
        logger.info("Password reset request received for: " + email);
        
        try {
            passwordResetService.requestPasswordReset(email);  // Utiliser l'email pour traiter la requête
            return ResponseEntity.ok("Password reset link sent");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process password reset request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process request");
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        logger.info("Received reset password request with token: " + resetPasswordDTO.getToken() + " and newPassword: " + resetPasswordDTO.getNewPassword());
        try {
            passwordResetService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
            return ResponseEntity.ok("Password has been reset");
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Password reset failed: " + e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDTO request, Authentication authentication) {
        try {
            // Récupérer le nom d'utilisateur actuel
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            // Appeler la méthode de changement de mot de passe du service
            boolean isChanged = passwordResetService.changePassword(username, request.getOldPassword(), request.getNewPassword());

            if (isChanged) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to change password");
            }
        } catch (RuntimeException e) {
            logger.severe("Change password failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

