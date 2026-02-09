package com.jennifertellez.library.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_API = "https://openlibrary.org/api/books?bibkeys=ISBN:";
    private final RestTemplate restTemplate;

    /**
     * Search OpenLibrary by ISBN
     */
    public Optional<Map<String, Object>> searchByIsbn(String isbn) {
        log.info("Searching OpenLibrary API for ISBN: {}", isbn);

        try {
            String url = OPEN_LIBRARY_API + isbn + "&format=json&jscmd=data";

            @SuppressWarnings("unchecked")
                    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && !response.isEmpty()) {
                String kay = "ISBN:" + isbn;
                if (response.containsKey(kay)) {
                    @SuppressWarnings("unchecked")
                            Map<String, Object> bookData = (Map<String, Object>) response.get(kay);
                    return Optional.of(bookData);
                }
            }
            log.info("No book found in OpenLibrary for ISBN: {}", isbn);
            return Optional.empty();

        } catch (RestClientException e) {
            log.error("Error calling OpenLibrary API for ISBN {}: {}", isbn, e.getMessage());
            return Optional.empty();
        }
    }
}
