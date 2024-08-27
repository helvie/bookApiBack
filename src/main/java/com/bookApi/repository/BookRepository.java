package com.bookApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookApi.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Méthode pour trouver des livres par ISBN
    List<Book> findByIsbn(String isbn);

}
