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

    /**
     * Search Google Books by ISBN (returns Optional for integration with manga detection)
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
     * Search Google Books by title (returns Optional for integration with manga detection)
     */
    public Optional<GoogleBooksResponse.BookItem> searchByTitle(String title) {
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
