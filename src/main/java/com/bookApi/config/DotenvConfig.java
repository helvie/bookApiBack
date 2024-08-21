package com.bookApi.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
	
//------------------------------ DOTENV -----------------------------

	
	// Bean permettant de charger les variables d'environnement depuis un fichier .env.
	// @return une instance de Dotenv configurée pour lire les variables d'environnement

    @Bean
    public Dotenv dotenv() {
        return Dotenv.load();
    }
}