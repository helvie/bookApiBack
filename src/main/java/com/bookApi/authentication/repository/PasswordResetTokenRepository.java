package com.bookApi.authentication.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookApi.authentication.entity.PasswordResetToken;
import com.bookApi.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    
    // Trouver un token par utilisateur et dont la date d'expiration est après une date donnée
    Optional<PasswordResetToken> findByUserAndExpiryDateAfter(User user, Date date);

    // Supprimer tous les tokens pour un utilisateur donné
    void deleteByUser(User user);
}

