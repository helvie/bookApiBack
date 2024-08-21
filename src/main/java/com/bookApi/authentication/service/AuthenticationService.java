package com.bookApi.authentication.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookApi.authentication.DTO.LoginUserDTO;
import com.bookApi.authentication.DTO.RegisterUserDTO;
import com.bookApi.authentication.DTO.UserResponseDTO;
import com.bookApi.authentication.entity.RefreshToken;
import com.bookApi.authentication.entity.VerificationToken;
import com.bookApi.authentication.repository.VerificationTokenRepository;
import com.bookApi.entity.Role;
import com.bookApi.entity.User;
import com.bookApi.repository.UserRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


 // Service pour la gestion de l'authentification des utilisateurs, de l'inscription et des tokens.

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthenticationEmailService emailService; 
    private final RefreshTokenService refreshTokenService;
    
    private static final int MAX_FAILED_ATTEMPTS = 3; 
    private static final long LOCK_TIME_DURATION = 15; // en minutes

    public AuthenticationService(
        UserRepository userRepository,
        VerificationTokenRepository verificationTokenRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationEmailService emailService,
        RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }
  
//----------------------------------- SIGNUP -----------------------------------

    // Inscription d'un nouvel utilisateur et envoi d'un email de vérification.
    // @param input Données d'inscription.
    // @return UserResponseDTO contenant les tokens JWT et de rafraîchissement.
    
    public UserResponseDTO signup(RegisterUserDTO input) {
        try {
            // Création et enregistrement d'un nouvel utilisateur
            User user = new User();
            user.setFirstname(input.getFirstname());
            user.setLastname(input.getLastname());
            user.setEmail(input.getEmail());
            user.setPassword(passwordEncoder.encode(input.getPassword()));
            user.setRole(Role.USER);
            user.setEnabled(false); // Le compte est désactivé jusqu'à vérification

            // Enregistrement de l'utilisateur dans la base de données
            User savedUser = userRepository.save(user);

            // Génération d'un token de vérification
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, savedUser, new Date(System.currentTimeMillis() + 86400000)); // Expiration dans 24 heures
            verificationTokenRepository.save(verificationToken);

            // Construction du lien de vérification
            String verificationLink = "http://localhost:8084/api/auth/verify?token=" + token;

            // Envoi de l'email de vérification via le service EmailService
            emailService.sendEmail(
                savedUser.getEmail(), 
                "Verification Email", 
                "Please verify your account using this link: " + verificationLink
            );

            // Génération du token JWT pour l'utilisateur
            String jwtToken = jwtService.generateToken(savedUser);
            String roleString = savedUser.getRole().name();

            // Génération du token de rafraîchissement pour l'utilisateur
            String refreshToken = refreshTokenService.createRefreshToken(savedUser).getToken();

            // Retour d'un UserResponseDTO avec les tokens JWT et de rafraîchissement
            return new UserResponseDTO(
                savedUser.getFirstname(),
                savedUser.getLastname(),
                savedUser.getEmail(),
                roleString,
                jwtToken,
                refreshToken
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'inscription : " + e.getMessage());
        }
    }
    
//--------------------------------- AUTHENTICATE ---------------------------------


    // Authentification d'un utilisateur et génération des tokens JWT et de rafraîchissement.
    // @param input Données de connexion.
    // @return UserResponseDTO contenant les tokens JWT et de rafraîchissement.
     
    public UserResponseDTO authenticate(LoginUserDTO input) {
        try {
            // Chargement de l'utilisateur depuis la base de données
            User user = userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Vérification si le compte est verrouillé
            if (user.isAccountLocked()) {
                if (unlockTimeExpired(user)) {
                    unlockAccount(user);
                } else {
                    throw new RuntimeException("Compte verrouillé. Réessayez plus tard.");
                }
            }

            // Authentification de l'utilisateur
            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                    )
                );

                // Réinitialisation des tentatives échouées après une connexion réussie
                resetFailedAttempts(user);

            } catch (Exception e) {
                // Incrémentation des tentatives échouées et verrouillage du compte si nécessaire
                increaseFailedAttempts(user);
                throw new RuntimeException("Échec de l'authentification : " + e.getMessage());
            }

            // Génération du token JWT
            String jwtToken = jwtService.generateToken(user);
            String roleString = user.getRole().name();

            // Génération du token de rafraîchissement
            RefreshToken refreshTokenObj = refreshTokenService.createRefreshToken(user);
            String refreshToken = refreshTokenObj.getToken();

            // Retour d'un UserResponseDTO
            return new UserResponseDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                roleString,
                jwtToken,
                refreshToken
            );

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'authentification : " + e.getMessage());
        }
    }
    
    
//---------------------------- INCREASE FAILED ATTEMPS ----------------------------


    // Augmentation du nombre de tentatives échouées et verrouillage du compte si nécessaire.
    // @param user Utilisateur dont les tentatives échouées sont incrémentées.

    private void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailAttempts);
        if (newFailAttempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount(user);
        }
        userRepository.save(user);
    }

  //----------------------------- RESET FAILED ATTEMPS -----------------------------
    
    // Réinitialisation du nombre de tentatives échouées à zéro.
    // @param user Utilisateur dont les tentatives échouées sont réinitialisées.

    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        userRepository.save(user);
    }
    
  //--------------------------------- LOCK ACCOUNT ---------------------------------

    // Verrouillage le compte de l'utilisateur.
    // @param user Utilisateur dont le compte est verrouillé.
    
    private void lockAccount(User user) {
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now());
        userRepository.save(user);
    }

  
//------------------------------ UNLOCK TIME EXPIRED --------------------------------

     // Vérification si la durée de verrouillage du compte est expirée.
     // @param user Utilisateur dont le compte est verrouillé.
     // @return true si la durée de verrouillage est expirée, false sinon.
     
    private boolean unlockTimeExpired(User user) {
        return ChronoUnit.MINUTES.between(user.getLockTime(), LocalDateTime.now()) >= LOCK_TIME_DURATION;
    }


    
//-------------------------------- UNLOCK ACCOUNT -----------------------------------
    
    // Déverrouillage du compte de l'utilisateur.
    // @param user Utilisateur dont le compte est déverrouillé.

    private void unlockAccount(User user) {
        user.setAccountLocked(false);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    
//-------------------------------- REFRESH TOKEN -----------------------------------

    // Actualisation du token JWT en utilisant le token de rafraîchissement.
    // @param token Token de rafraîchissement.
    // @return ResponseEntity contenant le nouveau token JWT ou une erreur.
    
    public ResponseEntity<?> refreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(token);

        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();
            if (refreshTokenService.verifyExpiration(refreshToken)) {
                User user = refreshToken.getUser();
                String newJwt = jwtService.generateToken(user);
                String roleString = user.getRole().name();

                return ResponseEntity.ok(
                    new UserResponseDTO(
                        user.getFirstname(), 
                        user.getLastname(), 
                        user.getEmail(), 
                        roleString, 
                        newJwt,
                        token // Fournit le même token de rafraîchissement pour le client
                    ));
            } else {
                return ResponseEntity.status(403).body("Token de rafraîchissement expiré");
            }
        } else {
            return ResponseEntity.status(403).body("Token de rafraîchissement invalide");
        }
    }
}
