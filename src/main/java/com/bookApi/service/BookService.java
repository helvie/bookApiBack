package com.bookApi.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookService {
	
    @Value("${external.api.book.url}")
    private String externalApiBookUrl;
    
    private final RestTemplate restTemplate;

    @Autowired
    public BookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Récupère les données d'un livre à partir d'une API externe en utilisant l'ISBN.
     *
     * @param isbn L'ISBN du livre à rechercher.
     * @return Une map contenant les données du livre.
     */
    public Map<String, Object> fetchBookDataFromApi(String isbn) {
        // Construire l'URL complète en ajoutant l'ISBN à l'URL de base
        String url = String.format(externalApiBookUrl, isbn);        
        // Effectuer la requête HTTP GET à l'API externe
        return restTemplate.getForObject(url, HashMap.class);
    }
}

    


