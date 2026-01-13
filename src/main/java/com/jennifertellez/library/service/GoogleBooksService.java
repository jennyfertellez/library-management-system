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

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleBooksService {

    private final RestTemplate restTemplate;
    private final BookService bookService;

    private static final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

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
                    :null);
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
}
