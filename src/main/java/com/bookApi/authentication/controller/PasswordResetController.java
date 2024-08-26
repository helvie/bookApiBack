package com.bookApi.authentication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.bookApi.authentication.DTO.ChangePasswordRequestDTO;
import com.bookApi.authentication.DTO.RequestResetPasswordDTO;
import com.bookApi.authentication.DTO.ResetPasswordDTO;
import com.bookApi.authentication.service.PasswordResetService;
import com.bookApi.exception.UserNotFoundException;

import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@RestController
@RequestMapping("/api/user")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private static final Logger logger = Logger.getLogger(PasswordResetController.class.getName());

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    //----------------------- DEMANDE DE RÉINITIALISATION DE MOT DE PASSE -----------------------

    // Endpoint pour demander une réinitialisation de mot de passe
    // @param request contient l'email pour lequel une réinitialisation est demandée
    // @return ResponseEntity<String> contenant un message de confirmation ou d'erreur
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody RequestResetPasswordDTO request) {
        
        // Récupération de l'email depuis l'objet request
        String email = request.getEmail(); 
        logger.info("Demande de réinitialisation de mot de passe reçue pour : " + email);

        try {
            // Traitement de la demande de réinitialisation
            passwordResetService.requestPasswordReset(email); 
            return ResponseEntity.ok("Lien de réinitialisation de mot de passe envoyé");
        } catch (UserNotFoundException e) {
            // Capturer et gérer l'exception UserNotFoundException
            logger.log(Level.WARNING, "Utilisateur non trouvé pour l'email : " + email, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé pour l'email fourni.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Échec du traitement de la demande de réinitialisation de mot de passe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec du traitement de la demande.");
        }
    }
    
    //---------------- RÉINITIALISATION DE MOT DE PASSE (SANS ETRE IDENTIFIE) ------------------

    // Endpoint pour réinitialiser le mot de passe
    // @param resetPasswordDTO contient le token de réinitialisation et le nouveau mot de passe
    // @return ResponseEntity<String> contenant un message de confirmation ou d'erreur
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        logger.info("Demande de réinitialisation de mot de passe reçue avec le token : " + resetPasswordDTO.getToken());

        try {
            passwordResetService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Mot de passe réinitialisé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Échec de la réinitialisation du mot de passe : " + e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur lors de la réinitialisation du mot de passe : " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
    }
    
    //------------------- CHANGEMENT DE MOT DE PASSE (EN ETANT IDENTIFIE) ----------------------

    // Endpoint pour changer le mot de passe de l'utilisateur connecté
    // @param request contient l'ancien mot de passe et le nouveau mot de passe
    // @param authentication contient les informations de l'utilisateur authentifié
    // @return ResponseEntity<String> contenant un message de confirmation ou d'erreur
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDTO request, Authentication authentication) {
        logger.info("Tentative de changement de mot de passe pour l'utilisateur authentifié");

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warning("Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        try {
            // Récupérer le nom d'utilisateur de l'utilisateur authentifié
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            logger.info("Utilisateur authentifié : " + username);

            // Appeler le service pour changer le mot de passe
            boolean isChanged = passwordResetService.changePassword(username, request.getOldPassword(), request.getNewPassword());

            if (isChanged) {
                return ResponseEntity.ok("Mot de passe changé avec succès");
            } else {
                return ResponseEntity.badRequest().body("Échec du changement de mot de passe");
            }
        } catch (Exception e) {
            logger.severe("Échec du changement de mot de passe : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue : " + e.getMessage());
        }
    }
}


