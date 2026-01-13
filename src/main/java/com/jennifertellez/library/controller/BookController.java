package com.jennifertellez.library.controller;

import com.jennifertellez.library.dto.BookResponse;
import com.jennifertellez.library.dto.CreateBookRequest;
import com.jennifertellez.library.dto.UpdateBookRequest;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.service.BookService;
import com.jennifertellez.library.service.GoogleBooksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final GoogleBooksService googleBooksService;

    //Create a new book entity
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        log.info("POST /api/books - Creating new book");
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Create a new book entity using Google API
    @PostMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponse> createBookByIsbn(@PathVariable String isbn) {
        log.info("POST /api/books/isbn/{} - Creating book from ISBN", isbn);
        BookResponse response = googleBooksService.fetchAndCreateBooksByIsbn(isbn);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Get all books in library
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        log.info("GET /api/books - Fetching all books");
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    //Get a book by id
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookId(@PathVariable Long id) {
        log.info("GET /api/books/{} - Fetching book by ID", id);
        BookResponse response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    //Get books by its status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookResponse>> getBooksByStatus(@PathVariable ReadingStatus status) {
        log.info("GET /api/books/status/{} - Fetching books by status", status);
        List<BookResponse> books = bookService.getBooksByStatus(status);
        return ResponseEntity.ok(books);
    }

    //Update a book
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        log.info("PUT /api/books/{} - Updating book", id);
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    //Delete book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("DELETE /api/books/{} - Deleting book", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    //Search for a specific book
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam String term) {
        log.info("GET /api/books/search?term={} - Seraching books", term);
        List<BookResponse> books = bookService.searchBooks(term);
        return ResponseEntity.ok(books);
    }
}
