package com.bookApi.authentication.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.bookApi.authentication.service.JwtService;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Constructeur d'initialisation des services nécessaires au filtre d'authentification JWT
    public JwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService,
        HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // Vérification de la présence et du format de l'en-tête d'autorisation
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraction du JWT de l'en-tête
            final String jwt = authHeader.substring(7); 

            // Extraction de l'email de l'utilisateur depuis le JWT
            final String userEmail = jwtService.extractUsername(jwt); 

            // Obtention de l'authentification actuelle dans le contexte de sécurité
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Vérification si l'email de l'utilisateur est non nul et aucune authentification n'est présente
            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); // Chargement des détails de l'utilisateur

                // Validation du JWT pour l'utilisateur
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Création d'un objet d'authentification pour Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken); // Définition de l'authentification dans le contexte de sécurité
                }
            }

            // Passage de la requête au prochain filtre dans la chaîne
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            // Gestion des exceptions éventuelles
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}


