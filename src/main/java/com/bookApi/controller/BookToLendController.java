package com.bookApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookApi.entity.BookToLend;
import com.bookApi.service.BookToLendService;

@RestController
@RequestMapping("/api/books")
public class BookToLendController {

    private final BookToLendService bookToLendService;

    @Autowired
    public BookToLendController(BookToLendService bookToLendService) {
        this.bookToLendService = bookToLendService;
    }

    // Endpoint pour ajouter un nouveau livre pour prêt
    @PostMapping
    public ResponseEntity<BookToLend> addBookToLend(@RequestBody BookToLend bookToLend) {
        BookToLend savedBook = bookToLendService.saveBookToLend(bookToLend);
        return ResponseEntity.ok(savedBook);
    }
}
