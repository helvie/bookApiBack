package com.bookApi.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.bookApi.service.EmailService;

import java.util.logging.Logger;

@Service
public class AuthenticationEmailService {

    private static final Logger log = Logger.getLogger(AuthenticationEmailService.class.getName());

    private final TemplateEngine templateEngine;
    private final EmailService emailService;  // Service général d'envoi d'emails

    @Autowired
    public AuthenticationEmailService(TemplateEngine templateEngine, EmailService emailService) {
        this.templateEngine = templateEngine;
        this.emailService = emailService;
    }

    //------------------------------- SEND EMAIL -------------------------------

    // Méthode générique pour envoyer des emails dans le cadre de l'authentification.
    public boolean sendEmail(String recipientEmail, String subject, String htmlBody) {
        return emailService.sendEmail(recipientEmail, subject, htmlBody);
    }

    //---------------------------- SEND VERIFICATION EMAIL ----------------------------
    
    public void sendVerificationEmail(String recipientEmail, String name, String verificationLink) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("verificationLink", verificationLink);
        String htmlBody = templateEngine.process("verification-email", context);
        sendEmail(recipientEmail, "Veuillez vérifier votre adresse email", htmlBody);
    }

    //--------------------------- SEND PASSWORD RESET EMAIL ---------------------------
    
    public boolean sendPasswordResetEmail(String recipientEmail, String name, String resetLink) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetLink", resetLink);
        String htmlBody = templateEngine.process("reset-password-email", context);

        log.info("Corps de l'email généré : " + htmlBody);

        boolean emailSent = sendEmail(recipientEmail, "Demande de réinitialisation de mot de passe", htmlBody);
        if (emailSent) {
            log.info("Email de réinitialisation envoyé à " + recipientEmail);
        } else {
            log.severe("Échec de l'envoi de l'email de réinitialisation à " + recipientEmail);
        }
        return emailSent;
    }
    
    //----------------- SEND REGISTRATION CONFIRMATION EMAIL ------------------------

    public void sendRegistrationConfirmationEmail(String recipientEmail, String name) {
        Context context = new Context();
        context.setVariable("name", name);
        String htmlBody = templateEngine.process("registration-confirmation-email", context);
        sendEmail(recipientEmail, "Confirmation d'inscription", htmlBody);
    }
}
