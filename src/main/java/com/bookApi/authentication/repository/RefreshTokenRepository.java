package com.bookApi.authentication.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bookApi.authentication.entity.RefreshToken;
import com.bookApi.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
