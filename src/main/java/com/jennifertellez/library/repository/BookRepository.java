package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByStatus(ReadingStatus status);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> searchBooks(@Param("searchTerm") String searchTerm);

}
