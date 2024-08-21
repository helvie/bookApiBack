package com.bookApi.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bookApi.authentication.DTO.UserAuthenticationDTO;
import com.bookApi.entity.User;
import com.bookApi.repository.UserRepository;

@Configuration
public class ApplicationConfiguration {

    private final UserRepository userRepository;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------------------------- USER DETAILS SERVICE ----------------------------
     
    // Service chargeant les données spécifiques à l'utilisateur pendant l'authentification.
    // @return un UserDetailsService qui charge les détails de l'utilisateur depuis le UserRepository
    
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map((User user) -> new UserAuthenticationDTO(
                        user.getEmail(),
                        user.getPassword(),
                        user.getAuthorities())) // Assurez-vous que getAuthorities() renvoie une liste adéquate
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // ------------------------------ PASSWORD ENCODER ------------------------------

    // Codeur utilisé pour encoder les mots de passe avant stockage et vérifier les mots de passe pendant l'authentification.
    // @return une instance de BCryptPasswordEncoder
    
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // --------------------------- AUTHENTICATION MANAGER ---------------------------

    // Gestionnaire responsable du traitement des demandes d'authentification. 
    // @param config la configuration d'authentification utilisée pour récupérer le AuthenticationManager
    // @return une instance de AuthenticationManager
    // @throws Exception si une erreur survient lors de la récupération du AuthenticationManager

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // --------------------------- AUTHENTICATION PROVIDER --------------------------
    
    // Fournisseur utilisé pour authentifier les utilisateurs avec les détails et le codeur de mot de passe spécifiés.
    // @return une instance de DaoAuthenticationProvider configurée avec le UserDetailsService et le BCryptPasswordEncoder

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}

