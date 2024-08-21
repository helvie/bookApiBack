package com.bookApi.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.bookApi.authentication.service.JwtService;

import org.springframework.http.HttpMethod;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    // Constructeur pour initialiser les services nécessaires à la configuration de sécurité
    
    public SecurityConfiguration(JwtService jwtService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    //---------------------------- CHAÎNE DE FILTRES DE SÉCURITÉ ----------------------------

    // Configuration de la chaîne de filtres de sécurité
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	// Désactivation de la protection CSRF
            .csrf(csrf -> csrf.disable())
            
            // Configuration de CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .authorizeHttpRequests(auth -> auth
            		
            	// Autorisation de toutes les requêtes OPTIONS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers(
                    "/api/auth/forgot-password",
                    "/api/user/signup", 
                    "/api/user/login", 
                    "/api/user/verify", 
                    "/api/auth/verify",
                    "/api/auth/reset-password"
                    
                 // Autorisation des requêtes vers ces endpoints
                ).permitAll() 
                
                // Authentification requise pour toutes les autres requêtes
                .anyRequest().authenticated() 
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configuration pour les sessions sans état
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Ajout du filtre d'authentification JWT

        return http.build();
    }

    //--------------------------- FILTRE D'AUTHENTIFICATION JWT ---------------------------

    // Définition du filtre d'authentification JWT
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService, handlerExceptionResolver);
    }

    //-------------------------- SOURCE DE CONFIGURATION CORS ----------------------------

    // Configuration des paramètres CORS
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        
        // Méthodes HTTP autorisées 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // En-têtes autorisés 
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        // En-têtes exposés 
        configuration.setExposedHeaders(List.of("Authorization")); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Enregistrement de la configuration CORS pour toutes les requêtes

        return source;
    }
}


