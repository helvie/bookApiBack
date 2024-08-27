package com.bookApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookApi.service.BookService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint pour récupérer les données d'un livre par son ISBN, auteur, ou titre.
     *
     * @param isbn L'ISBN du livre à rechercher.
     * @param authors Les mots clés de l'auteur à rechercher.
     * @param titles Les mots clés à rechercher dans le titre.
     * @return Une réponse contenant les données du livre ou un statut 404 si non trouvé.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBook(
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) List<String> authors,
            @RequestParam(required = false) List<String> titles) {
        
        Map<String, Object> bookData = bookService.fetchBookFromApi(isbn, authors, titles);
        if (bookData != null && !bookData.isEmpty()) {
            return ResponseEntity.ok(bookData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}