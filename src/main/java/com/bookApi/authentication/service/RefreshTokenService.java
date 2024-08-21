package com.bookApi.authentication.service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookApi.authentication.entity.RefreshToken;
import com.bookApi.authentication.repository.RefreshTokenRepository;
import com.bookApi.entity.User;

@Service
public class RefreshTokenService {

    @Value("${security.jwt.refresh-token-expiration-time}")
    private Long refreshTokenDurationMs;  // Durée d'expiration des tokens de rafraîchissement (en millisecondes)

    private final RefreshTokenRepository refreshTokenRepository;  // Répository pour les tokens de rafraîchissement

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;  // Initialisation du répository des tokens
    }

    //----------------------------- FIND BY TOKEN -----------------------------

    // @param token Valeur du token à rechercher.
    // @return Un Optional contenant le token de rafraîchissement si trouvé, sinon vide.
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);  // Recherche du token dans la base de données
    }

    //------------------------------ CREATE REFRESH TOKEN ------------------------------
    
    // @param user Utilisateur pour lequel le token est créé.
    // @return Le token de rafraîchissement créé.
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);  // Associe le token à l'utilisateur
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenDurationMs));  // Définit la date d'expiration du token
        refreshToken.setToken(UUID.randomUUID().toString());  // Génère un token unique

        refreshToken = refreshTokenRepository.save(refreshToken);  // Sauvegarde le token dans la base de données
        return refreshToken;  // Retourne le token créé
    }

    //------------------------------ VERIFY EXPIRATION ------------------------------
    
    // @param token Token de rafraîchissement à vérifier.
    // @return true si le token n'est pas expiré, sinon false.
    public boolean verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().before(new Date())) {  // Vérifie si la date d'expiration est passée
            refreshTokenRepository.delete(token);  // Supprime le token expiré de la base de données
            return false;
        }
        return true;
    }

    //------------------------------ DELETE BY USER ------------------------------
    
    // Supprime tous les tokens de rafraîchissement associés à un utilisateur.
    // @param user Utilisateur dont les tokens doivent être supprimés.
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}

