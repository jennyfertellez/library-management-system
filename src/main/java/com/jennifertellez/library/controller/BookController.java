package com.jennifertellez.library.controller;

import com.jennifertellez.library.dto.*;
import com.jennifertellez.library.model.BookSearchCriteria;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.service.BookService;
import com.jennifertellez.library.service.GoogleBooksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Books", description = "Book management APIs")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final GoogleBooksService googleBooksService;

    @Operation(
            summary = "Create a new book",
            description = "Creates a new book with the provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Book with ISBN already exists")
    })
    //Create a new book entity
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        log.info("POST /api/books - Creating new book");
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponse> createBookByIsbn(@PathVariable String isbn) {
        log.info("POST /api/books/isbn/{} - Creating book from ISBN or title", isbn);

        // Check if it looks like an ISBN (only digits and hyphens)
        boolean isActualIsbn = isbn.matches("[0-9\\-]+");

        BookResponse response;
        if (isActualIsbn) {
            response = bookService.createBookFromIsbn(isbn);
        } else {
            // It's a title, search by title instead
            response = bookService.searchBookByTitle(isbn);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get all books with pagination and sorting",
            description = "Retrieves books with pagination, sorting, and optional filtering"
    )
    //Get all books in library
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) ReadingStatus status,
            @RequestParam(required = false) String author) {

        log.info("GET /api/books - page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        PageResponse<BookResponse> response;

        if (status != null) {
            log.info("Filtering by status: {}", status);
            response = bookService.getBooksByStatus(status, pageable);
        } else if (author != null && !author.isEmpty()) {
            log.info("Filtering by author: {}", author);
            response = bookService.getBooksByAuthor(author, pageable);
        } else {
            response = bookService.getAllBooks(pageable);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get book by ID",
            description = "Retrieves a specific book by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })

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

    @Operation(
            summary = "Update book",
            description = "Update book"
    )

    //Update a book
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        log.info("PUT /api/books/{} - Updating book", id);
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete book",
            description = "Delete book"
    )

    //Delete book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("DELETE /api/books/{} - Deleting book", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Search books with pagination",
            description = "Search books by title, author, or description with pagination support"
    )

    //Search for a specific book
    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> searchBooks(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy) {

        log.info("GET /api/books/search?term={}&page={}&size={} - Searching books", term, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        PageResponse<BookResponse> response = bookService.searchBooks(term, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Advanced search with multiple filters",
            description = "Search books with multiple criteria: term, status, author, rating range, year range, atc."
    )
    @GetMapping("/advanced-search")
    public ResponseEntity<PageResponse<BookResponse>> advanceSearch(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) ReadingStatus status,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Boolean hasIsbn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        log.info("GET /api/books/advance-search with filters");

        BookSearchCriteria criteria = BookSearchCriteria.builder()
                .searchTerm(term)
                .status(status)
                .author(author)
                .minRating(minRating)
                .maxRating(maxRating)
                .minYear(minYear)
                .maxYear(maxYear)
                .hasIsbn(hasIsbn)
                .build();

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        PageResponse<BookResponse> response = bookService.advanceSearch(criteria, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get Reading statistics",
            description = "Comprehensive statistics about your reading habits"
    )
    @GetMapping("/stats")
    public  ResponseEntity<ReadingStatsResponse> getStatistics() {
        log.info("GET /api/books/stats - Fetching reading statistics");
        ReadingStatsResponse stats = bookService.getReadingStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search/title")
    public ResponseEntity<BookResponse> searchByTitle(@RequestParam String title) {
        log.info("POST /api/books/search/title - Searching for: {}", title);
        BookResponse response = bookService.searchBookByTitle(title);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
