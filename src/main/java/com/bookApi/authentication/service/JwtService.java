package com.bookApi.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    
    @Value("${security.jwt.secret-key}")
    private String secretKey;  // Clé secrète pour signer les tokens

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;  // Temps d'expiration des JWT

    @Value("${security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpiration;  // Temps d'expiration des tokens de rafraîchissement
    
    //----------------------------- EXTRACT USERNAME -----------------------------
    
    // @param token Token JWT.
    // @return Nom d'utilisateur extrait
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //------------------------------ EXTRACT CLAIM -------------------------------
    
    // Extrait une revendication spécifique du token JWT
    // @param token Token JWT
    // @param claimsResolver Fonction pour extraire la revendication.
    // @param <T> Type de la revendication
    // @return Valeur de la revendication
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //------------------------------ GENERATE TOKEN ------------------------------
    
    // Génère un token JWT avec expiration par défaut
    // @param userDetails Détails de l'utilisateur
    // @return Token JWT généré
    
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    // Génère un token JWT avec expiration spécifiée
    // @param extraClaims Revendications supplémentaires
    // @param userDetails Détails de l'utilisateur
    // @param expiration Temps d'expiration en millisecondes
    // @return Token JWT généré
    
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return buildToken(extraClaims, userDetails, expiration);
    }

    // @param userDetails Détails de l'utilisateur
    // @return Token de rafraîchissement généré
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    // @return Temps d'expiration en millisecondes
    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //------------------------------ VALIDATE TOKEN ------------------------------
    
    // @param token Token JWT
    // @param userDetails Détails de l'utilisateur
    // @return true si le token est valide, sinon false
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
