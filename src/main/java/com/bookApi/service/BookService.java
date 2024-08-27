package com.bookApi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
     * Récupère les données d'un livre à partir d'une API externe en utilisant différents critères.
     *
     * @param isbn L'ISBN du livre à rechercher.
     * @param authors Les mots clés de l'auteur.
     * @param titles Les mots clés du titre.
     * @return Une map contenant les données du livre.
     */
    public Map<String, Object> fetchBookFromApi(String isbn, List<String> authors, List<String> titles) {
        StringJoiner query = new StringJoiner("+");
        
        if (isbn != null && !isbn.isEmpty()) {
            query.add("isbn:" + isbn);
        } else {
            if (authors != null && !authors.isEmpty()) {
                StringJoiner authorQuery = new StringJoiner("+");
                for (String author : authors) {
                    authorQuery.add(author);
                }
                query.add("inauthor:" + authorQuery.toString());
            }
            if (titles != null && !titles.isEmpty()) {
                StringJoiner titleQuery = new StringJoiner("+");
                for (String title : titles) {
                    titleQuery.add(title);
                }
                query.add("intitle:" + titleQuery.toString());
            }
        }
        
        String url = String.format(externalApiBookUrl, query.toString());
        System.out.println("Final URL: " + url);
        return restTemplate.getForObject(url, HashMap.class);
    }
}

    


