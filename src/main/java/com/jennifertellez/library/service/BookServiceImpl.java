package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.*;
import com.jennifertellez.library.exception.BookNotFoundException;
import com.jennifertellez.library.exception.DuplicateBookException;
import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.BookSearchCriteria;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.repository.BookRepository;
import com.jennifertellez.library.repository.BookSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookResponse createBook(CreateBookRequest request) {
        log.info("Creating a new book with title: {}", request.getTitle());

        //Check for duplicate ISBN if provided
        if (request.getIsbn() != null && !request.getIsbn().isEmpty()) {
            if (bookRepository.existsByIsbn(request.getIsbn())) {
                throw new DuplicateBookException(request.getIsbn());
            }
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPublishedDate(request.getPublishedDate());
        book.setPageCount(request.getPageCount());
        book.setThumbnail(request.getThumbnailUrl());
        book.setStatus(request.getStatus());
        book.setNotes(request.getNotes());

        Book savedBook = bookRepository.save(book);
        log.info("Book created successfully with ID: {}", savedBook.getId());

        return mapToResponse(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        log.info("Fetching book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return mapToResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByStatus(ReadingStatus status) {
        log.info("Fetching books with status: {}", status);
        return bookRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        log.info("Updating book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        //Update only non-null fields
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getDescription() != null) {
            book.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        }
        if (request.getFinishedDate() != null) {
            book.setFinishedDate(request.getFinishedDate());
        }
        if (request.getRating() != null) {
            book.setRating(request.getRating());
        }
        if (request.getNotes() != null) {
            book.setNotes(request.getNotes());
        }

        Book updatedBook = bookRepository.save(book);
        log.info("Book updated successfully with ID: {}", updatedBook.getId());

        return mapToResponse(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);

        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }

        bookRepository.deleteById(id);
        log.info("Book deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String searchTerm) {
        log.info("Searching books with term: {}", searchTerm);
        return bookRepository.searchBooks(searchTerm).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAllBooks(Pageable pageable) {
        log.info("Fetching books with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Book> bookPage = bookRepository.findAll(pageable);
        Page<BookResponse> responsePage = bookPage.map(this::mapToResponse);

        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooks(String searchTerm, Pageable pageable) {
        log.info("Searching books with term: '{}', page: {}, size: {}",
                searchTerm,pageable.getPageNumber(), pageable.getPageSize());

        Page<Book> bookPage = bookRepository.searchBooks(searchTerm, pageable);
        Page<BookResponse> responsePage = bookPage.map(this::mapToResponse);

        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooksByStatus(ReadingStatus status, Pageable pageable) {
        log.info("Fetching books with status: {}, page: {}", status, pageable.getPageNumber());

        Page<Book> bookPage = bookRepository.findByStatus(status, pageable);
        Page<BookResponse> responsePage = bookPage.map(this::mapToResponse);

        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooksByAuthor(String author, Pageable pageable) {
        log.info("Fetching books by author: {}, page: {}", author, pageable.getPageNumber());

        Page<Book> bookPage = bookRepository.findByAuthorContainingIgnoreCase(author, pageable);
        Page<BookResponse> responsePage = bookPage.map(this::mapToResponse);

        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> advanceSearch(BookSearchCriteria criteria, Pageable pageable) {
        log.info("Advance search with criteria: {}", criteria);

        Specification<Book> spec = BookSpecification.withCriteria(criteria);
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        Page<BookResponse> responsePage = bookPage.map(this::mapToResponse);

        return  new PageResponse<>(responsePage);
    }

    //Helper method to map Entity to DTO
    private BookResponse mapToResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setDescription(book.getDescription());
        response.setPublishedDate(book.getPublishedDate());
        response.setPageCount(book.getPageCount());
        response.setThumbnailUrl(book.getThumbnail());
        response.setStatus(book.getStatus());
        response.setFinishedDate(book.getFinishedDate());
        response.setRating(book.getRating());
        response.setNotes(book.getNotes());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ReadingStatsResponse getReadingStatistics() {
        log.info("Calculating reading statistics");

        List<Book> allBooks = bookRepository.findAll();
        int currentYear = Year.now().getValue();

        long totalBooks = allBooks.size();
        long booksRead = allBooks.stream()
                .filter(b -> b.getStatus() == ReadingStatus.FINISHED)
                .count();
        long booksToRead = allBooks.stream()
                .filter(b -> b.getStatus() == ReadingStatus.TO_READ)
                .count();
        long currentlyReading = allBooks.stream()
                .filter(b -> b.getStatus() == ReadingStatus.CURRENTLY_READING)
                .count();
        long booksDidNotFinish = allBooks.stream()
                .filter(b -> b.getStatus() == ReadingStatus.DNF)
                .count();

        //Current year statistics
        long booksReadThisYear = allBooks.stream()
                .filter(b -> b.getFinishedDate() != null &&
                        b.getFinishedDate().getYear() == currentYear)
                .count();

        long booksAddedThisYear = allBooks.stream()
                .filter(b -> b.getCreatedAt() != null &&
                        b.getCreatedAt().getYear() == currentYear)
                .count();

        //Rating statistics
        List<Book> ratedBooks = allBooks.stream()
                .filter(b -> b.getRating() != null)
                .collect(Collectors.toList());

        Double averageRating = ratedBooks.isEmpty() ? 0.0 :
                ratedBooks.stream()
                        .mapToInt(Book::getRating)
                        .average()
                        .orElse(0.0);

        Map<Integer, Long> ratingDistribution = ratedBooks.stream()
                .collect(Collectors.groupingBy(Book::getRating, Collectors.counting()));

        // Author statistics
        long uniqueAuthors = allBooks.stream()
                .map(Book::getAuthor)
                .filter(author -> author != null && !author.isEmpty())
                .distinct()
                .count();

        Map<String, Long> topAuthors = allBooks.stream()
                .filter(b -> b.getAuthor() != null && !b.getAuthor().isEmpty())
                .collect(Collectors.groupingBy(Book::getAuthor, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Reading pace (books per month this year)
        int monthsElapsed = LocalDate.now().getMonthValue();
        double booksPerMonth = monthsElapsed > 0 ?
                (double) booksReadThisYear / monthsElapsed : 0.0;

        // Average pages per book
        double averagePagesPerBook = allBooks.stream()
                .filter(b -> b.getPageCount() != null && b.getPageCount() > 0)
                .mapToInt(Book::getPageCount)
                .average()
                .orElse(0.0);

        // Books read by year
        Map<Integer, Long> booksReadByYear = allBooks.stream()
                .filter(b -> b.getFinishedDate() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getFinishedDate().getYear(),
                        Collectors.counting()
                ));

        // Build response
        return ReadingStatsResponse.builder()
                .totalBooks(totalBooks)
                .booksRead(booksRead)
                .booksToRead(booksToRead)
                .currentlyReading(currentlyReading)
                .booksDidNotFinish(booksDidNotFinish)
                .currentYear(currentYear)
                .booksReadThisYear(booksReadThisYear)
                .booksAddedThisYear(booksAddedThisYear)
                .averageRating(Math.round(averageRating * 10.0) / 10.0) // Round to 1 decimal
                .ratedBooks((long) ratedBooks.size())
                .ratingDistribution(ratingDistribution)
                .uniqueAuthors(uniqueAuthors)
                .topAuthors(topAuthors)
                .booksPerMonth(Math.round(booksPerMonth * 10.0) / 10.0)
                .averagePagesPerBook((double) Math.round(averagePagesPerBook))
                .booksReadByYear(booksReadByYear)
                .build();
    }

}
