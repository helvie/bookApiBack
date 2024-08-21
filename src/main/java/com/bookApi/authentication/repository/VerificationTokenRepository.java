package com.bookApi.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookApi.authentication.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}