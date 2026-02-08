package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.BookResponse;
import com.jennifertellez.library.dto.CreateBookRequest;
import com.jennifertellez.library.dto.GoogleBooksResponse;
import com.jennifertellez.library.model.ReadingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleBooksService {

    private static final String GOOGLE_BOOKS_API_BASE = "https://www.googleapis.com/books/v1/volumes";
    private static final String GOOGLE_BOOKS_API = GOOGLE_BOOKS_API_BASE + "?q=isbn:";
    private final RestTemplate restTemplate;
    private final BookService bookService;

    public BookResponse fetchAndCreateBooksByIsbn(String isbn) {
        log.info("Fetching book details from Google Books API for ISBN: {}", isbn);

        try {
            String url = GOOGLE_BOOKS_API + isbn;
            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                throw new RuntimeException("No book found with ISBN: " + isbn);
            }

            GoogleBooksResponse.VolumeInfo volumeInfo = response.getItems().get(0).getVolumeInfo();

            CreateBookRequest request = new CreateBookRequest();
            request.setIsbn(isbn);
            request.setTitle(volumeInfo.getTitle());
            request.setAuthor(volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()
                    ? String.join(", ", volumeInfo.getAuthors())
                    : null);
            request.setDescription(volumeInfo.getDescription());
            request.setPublishedDate(volumeInfo.getPublishedDate());
            request.setPageCount(volumeInfo.getPageCount());
            request.setThumbnailUrl(volumeInfo.getImageLinks() != null
                    ? volumeInfo.getImageLinks().getThumbnail()
                    : null);
            request.setStatus(ReadingStatus.TO_READ);

            log.info("Successfully fetched book details: {}", volumeInfo.getTitle());
            return bookService.createBook(request);

        } catch (RestClientException e) {
            log.error("Error calling Google Books API: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch book from Google Books API: " + e.getMessage());
        }
    }

    /**
     * Search Google Books by ISBN (returns optional for manga detection)
     */
    public Optional<GoogleBooksResponse.BookItem> searchByIsbn(String isbn) {
        log.info("Searching Google Books API for ISBN: {}", isbn);

        try {
            String url = GOOGLE_BOOKS_API + isbn;
            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                return Optional.of(response.getItems().get(0));
            }

            log.info("No book found in Google Books for ISBN: {}", isbn);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Error calling Google Books API for ISBN {}: {}", isbn, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Search Google Books by title (returns optional for manga)
     */
    public Optional<GoogleBooksResponse.BookItem> searchForTitle(String title) {
        log.info("Searching Google Books API for title: {}", title);

        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = GOOGLE_BOOKS_API_BASE + "?q=" + encodedTitle;

            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                return Optional.of(response.getItems().get(0));
            }

            log.info("No book found in Google Books for title: {}", title);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Error calling Google Books API for title {}: {}", title, e.getMessage());
            return Optional.empty();
        }
    }
}
