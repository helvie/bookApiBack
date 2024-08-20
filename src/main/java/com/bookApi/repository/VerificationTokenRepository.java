package com.bookApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookApi.entity.VerificationToken;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}