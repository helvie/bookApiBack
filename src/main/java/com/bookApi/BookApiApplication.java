package com.bookApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BookApiApplication {

	public static void main(String[] args) {
        // Charger les variables d'environnement depuis le fichier .env
        Dotenv dotenv = Dotenv.load();
//        System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));
        System.setProperty("MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
        System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
        System.setProperty("EXTERNAL_API_KEY", dotenv.get("EXTERNAL_API_KEY"));
        System.setProperty("JWT_SECRET_KEY", dotenv.get("JWT_SECRET_KEY"));
        System.setProperty("DATABASE_USERNAME", dotenv.get("DATABASE_USERNAME"));

        // Lancer l'application Spring Boot
		SpringApplication.run(BookApiApplication.class, args);
	}

}
