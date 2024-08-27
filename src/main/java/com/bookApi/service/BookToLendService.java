package com.bookApi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bookApi.entity.BookToLend;
import com.bookApi.repository.BookToLendRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BookToLendService {
	
    private final BookToLendRepository bookToLendRepository;
    private final BookService bookService;
    
    @Autowired
    public BookToLendService(BookToLendRepository bookToLendRepository, BookService bookService) {
        this.bookToLendRepository = bookToLendRepository;
        this.bookService = bookService;
    }
    
//    **********************************************************************************************************
    
//    public BookToLend createBookToLend(String isbn) {
//        // Utilisez le service BookService pour récupérer les données du livre à partir de l'API externe
//        Map<String, Object> response = bookService.fetchBookDataFromApi(isbn);
//
//        if (response != null && response.containsKey("items")) {
//            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
//            if (items != null && !items.isEmpty()) {
//                Map<String, Object> volumeInfo = (Map<String, Object>) items.get(0).get("volumeInfo");
//                Map<String, Object> searchInfo = (Map<String, Object>) items.get(0).get("searchInfo");
//
//                BookToLend bookToLend = new BookToLend();
//                bookToLend.setIsbn(isbn);
//
//                if (volumeInfo != null) {
//                    bookToLend.setTitle((String) volumeInfo.get("title"));
//                    bookToLend.setAuthors((List<String>) volumeInfo.get("authors"));
//                    bookToLend.setEditor((String) volumeInfo.get("publisher"));
//                    bookToLend.setNumberOfPages((Integer) volumeInfo.get("pageCount"));
//                    bookToLend.setFormat((String) volumeInfo.get("printType"));
//
//                    String publicationDateString = (String) volumeInfo.get("publishedDate");
//                    if (publicationDateString != null) {
//                        try {
//                            Date publicationDate = new SimpleDateFormat("yyyy-MM-dd").parse(publicationDateString);
//                            bookToLend.setPublicationDate(publicationDate);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                if (searchInfo != null) {
//                    bookToLend.setDescription((String) searchInfo.get("textSnippet"));
//                }
//
//                return bookToLend;
//            }
//        }
//        return null;
//    }    
//  **********************************************************************************************************

    public BookToLend saveBookToLend(BookToLend bookToLend) {
        return bookToLendRepository.save(bookToLend);
    }
    
}

