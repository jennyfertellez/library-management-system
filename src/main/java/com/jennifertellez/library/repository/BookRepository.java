package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

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

    //Search books with pagination support
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> searchBooks(@Param("searchTerm") String searchTerm, Pageable pageable);

    //Find books by status with pagination
    Page<Book> findByStatus(ReadingStatus status, Pageable pageable);

    //Find books by author with pagination
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    List<Book> findByStatusAndFinishedDateBetween(ReadingStatus status, LocalDate startDate, LocalDate endDate);

    Optional<Book> findByMalId(Long malId);

}
