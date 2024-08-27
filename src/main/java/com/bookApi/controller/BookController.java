package com.bookApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookApi.service.BookService;

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
     * Endpoint pour récupérer les données d'un livre par son ISBN.
     *
     * @param isbn L'ISBN du livre à rechercher.
     * @return Une réponse contenant les données du livre ou un statut 404 si non trouvé.
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Map<String, Object>> getBookByIsbn(@PathVariable String isbn) {
        Map<String, Object> bookData = bookService.fetchBookDataFromApi(isbn);
        if (bookData != null && !bookData.isEmpty()) {
            return ResponseEntity.ok(bookData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}