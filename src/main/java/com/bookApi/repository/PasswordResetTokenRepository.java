package com.bookApi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookApi.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}

