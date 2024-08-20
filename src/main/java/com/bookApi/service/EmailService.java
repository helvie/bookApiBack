package com.bookApi.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailService {
    
    private static final Logger log = Logger.getLogger(EmailService.class.getName());

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    private final TemplateEngine templateEngine;
    private Session mailSession;

    @Autowired
    public EmailService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @PostConstruct
    private void init() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", mailHost);
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", mailPort);
        props.put("mail.debug", "true");

        this.mailSession = Session.getInstance(props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, mailPassword);
                }
            });
    }
    
    
    
    public boolean sendEmail(String recipientEmail, String subject, String htmlBody) {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(message);
            log.info("Email sent successfully!");
            return true;  // Email sent successfully
        } catch (MessagingException e) {
            log.log(Level.SEVERE, "Failed to send email: {0}", e.getMessage());
            return false; // Failed to send email
        }
    }

    public void sendVerificationEmail(String recipientEmail, String name, String verificationLink) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("verificationLink", verificationLink);
        String htmlBody = templateEngine.process("verification-email", context);
        sendEmail(recipientEmail, "Please verify your email address", htmlBody);
    }

    public boolean sendPasswordResetEmail(String recipientEmail, String name, String resetLink) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetLink", resetLink);
        String htmlBody = templateEngine.process("reset-password-email", context);
        
        log.info("Generated email body: " + htmlBody);
        
        boolean emailSent = sendEmail(recipientEmail, "Password Reset Request", htmlBody);
        if (emailSent) {
            log.info("Password reset email sent to " + recipientEmail);
        } else {
            log.severe("Failed to send password reset email to " + recipientEmail);
        }
        return emailSent;
    }

    public void sendRegistrationConfirmationEmail(String recipientEmail, String name) {
        Context context = new Context();
        context.setVariable("name", name);
        String htmlBody = templateEngine.process("registration-confirmation-email", context);
        sendEmail(recipientEmail, "Registration Confirmation", htmlBody);
    }
}

