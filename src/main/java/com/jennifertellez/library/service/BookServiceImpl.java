package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.*;
import com.jennifertellez.library.dto.jikan.JikanMangaResponse;
import com.jennifertellez.library.dto.jikan.JikanSingleMangaResponse;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final GoogleBooksService googleBooksService;
    private final JikanService jikanService;
    private final OpenLibraryService openLibraryService;

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

    /**
     * Check if the book is likely manga based on the title patterns
     */
    private boolean isManga(GoogleBooksResponse.VolumeInfo volumeInfo) {
        String title = volumeInfo.getTitle().toLowerCase();

        return title.contains("manga") ||
                title.contains("vol.") ||
                title.contains("vol ") ||
                title.contains("volume") ||
                title.matches(".*,\\s*vol\\.?\\s*\\d+.*") ||
                title.matches(".*\\d+$");
    }

    /**
     * Convert Jikan manga data to BookResponse
     */

    private BookResponse convertJikanToBookResponse(JikanMangaResponse.JikanMangaData manga) {
        BookResponse response = new BookResponse();

        //Use English title if available, otherwise use main title
        response.setTitle(manga.getTitleEnglish() != null ? manga.getTitleEnglish() : manga.getTitle());

        //Get first author
        if (manga.getAuthors() != null && !manga.getAuthors().isEmpty()) {
            response.setAuthor(manga.getAuthors().get(0).getName());
        }

        response.setDescription(manga.getSynopsis());

        // Use published data
        if (manga.getPublished() != null && manga.getPublished().getFrom() != null) {
            response.setPublishedDate(manga.getPublished().getFrom().substring(0, 10));
        }

        // Use chapters as page count (approximate)
        if (manga.getImages() != null && manga.getImages().getJpg() != null) {
            response.setThumbnailUrl(manga.getImages().getJpg().getLargeImageUrl() != null
            ? manga.getImages().getJpg().getLargeImageUrl()
                    : manga.getImages().getJpg().getImageUrl());
        }

        response.setIsbn("MAL-" + manga.getMalId());

        //Default status
        response.setStatus(ReadingStatus.TO_READ);

        return response;

    }

    /**
     * Enhanced ISBN lookup with OpenLibrary and Google Books fallback
     */
    @Override
    public BookResponse createBookFromIsbn(String isbn) {
        log.info("Looking up book/manga with ISBN: {}", isbn);

        // Clean the ISBN
        String cleanIsbn = isbn.replaceAll("[\\s\\-]", "");

        // Validate ISBN format
        if (!cleanIsbn.matches("^\\d{10}(\\d{3})?$")) {
            throw new RuntimeException("Invalid ISBN format. ISBN must be 10 or 13 digits.");
        }

        // Step 1: Try OpenLibrary first (no rate limits!)
        Optional<Map<String, Object>> openLibraryBook = openLibraryService.searchByIsbn(cleanIsbn);

        if (openLibraryBook.isPresent()) {
            log.info("Found book in OpenLibrary");
            Book book = convertOpenLibraryToEntity(openLibraryBook.get(), cleanIsbn);
            Book saved = bookRepository.save(book);
            return mapToResponse(saved);
        }

        // Step 2: Try Google Books (might be rate limited)
        try {
            Optional<GoogleBooksResponse.BookItem> googleBook = googleBooksService.searchByIsbn(cleanIsbn);

            if (googleBook.isPresent()) {
                GoogleBooksResponse.VolumeInfo volumeInfo = googleBook.get().getVolumeInfo();

                // Check if it's manga
                if (isManga(volumeInfo)) {
                    log.info("Detected manga from Google Books: {}", volumeInfo.getTitle());

                    Optional<JikanMangaResponse.JikanMangaData> jikanManga =
                            jikanService.searchMangaByTitle(volumeInfo.getTitle());

                    if (jikanManga.isPresent()) {
                        log.info("Found better manga data in Jikan API");
                        BookResponse mangaResponse = convertJikanToBookResponse(jikanManga.get());
                        mangaResponse.setIsbn(cleanIsbn);
                        Book book = convertResponseToEntity(mangaResponse);
                        Book saved = bookRepository.save(book);
                        return mapToResponse(saved);
                    }
                }

                Book book = convertGoogleBookToEntity(volumeInfo);
                book.setIsbn(cleanIsbn);
                Book saved = bookRepository.save(book);
                return mapToResponse(saved);
            }
        } catch (Exception e) {
            log.warn("Google Books API failed (possibly rate limited): {}", e.getMessage());
        }

        // Step 3: All APIs failed
        throw new RuntimeException(
                "Book not found with ISBN: " + cleanIsbn +
                        ". Please try adding it manually or search by title if it's a manga."
        );
    }

    /**
     * Convert OpenLibrary data to Book entity
     */
    private Book convertOpenLibraryToEntity(Map<String, Object> data, String isbn) {
        Book book = new Book();

        // Title
        if (data.containsKey("title")) {
            book.setTitle((String) data.get("title"));
        }

        // Authors
        if (data.containsKey("authors")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> authors = (List<Map<String, Object>>) data.get("authors");
            if (authors != null && !authors.isEmpty()) {
                String authorNames = authors.stream()
                        .map(author -> (String) author.get("name"))
                        .collect(Collectors.joining(", "));
                book.setAuthor(authorNames);
            }
        }

        // Description
        if (data.containsKey("notes")) {
            book.setDescription((String) data.get("notes"));
        } else if (data.containsKey("excerpts")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> excerpts = (List<Map<String, Object>>) data.get("excerpts");
            if (excerpts != null && !excerpts.isEmpty()) {
                book.setDescription((String) excerpts.get(0).get("text"));
            }
        }

        // Published date
        if (data.containsKey("publish_date")) {
            book.setPublishedDate((String) data.get("publish_date"));
        }

        // Page count
        if (data.containsKey("number_of_pages")) {
            book.setPageCount((Integer) data.get("number_of_pages"));
        }

        // Cover image
        if (data.containsKey("cover")) {
            @SuppressWarnings("unchecked")
            Map<String, String> cover = (Map<String, String>) data.get("cover");
            if (cover != null && cover.containsKey("medium")) {
                book.setThumbnail(cover.get("medium"));
            }
        }

        book.setIsbn(isbn);
        book.setStatus(ReadingStatus.TO_READ);

        return book;
    }

    /**
     * Search for books with manga support
     */
    @Override
    public BookResponse searchBookByTitle(String title) {
        log.info("Searching for book/manga with title: {}", title);

        // First try Google Books
        Optional<GoogleBooksResponse.BookItem> googleBook = googleBooksService.searchByTitle(title);

        if (googleBook.isPresent()) {
            GoogleBooksResponse.VolumeInfo volumeInfo = googleBook.get().getVolumeInfo();

            // Check if it's manga
            if (isManga(volumeInfo)) {
                log.info("Detected manga from Google Books, attempting Jikan lookup");

                Optional<JikanMangaResponse.JikanMangaData> jikanManga =
                        jikanService.searchMangaByTitle(volumeInfo.getTitle());

                if (jikanManga.isPresent()) {
                    return convertJikanToBookResponse(jikanManga.get());
                }
            }

            return convertGoogleBookToResponse(volumeInfo);
        }

        // Try Jikan if Google Books failed
        log.info("Google Books returned no results, trying Jikan API for: {}", title);
        Optional<JikanMangaResponse.JikanMangaData> jikanManga = jikanService.searchMangaByTitle(title);

        if (jikanManga.isPresent()) {
            return convertJikanToBookResponse(jikanManga.get());
        }

        throw new RuntimeException("Book not found in Google Books or Jikan API with title: " + title);
    }

    /**
     * Convert BookResponse to Entity
     */
    private Book convertResponseToEntity(BookResponse response) {
        Book book = new Book();
        book.setTitle(response.getTitle());
        book.setAuthor(response.getAuthor());
        book.setIsbn(response.getIsbn());
        book.setDescription(response.getDescription());
        book.setPublishedDate(response.getPublishedDate());
        book.setPageCount(response.getPageCount());
        book.setThumbnail(response.getThumbnailUrl());
        book.setStatus(response.getStatus());
        return book;
    }

    /**
     * Convert Google Books VolumeInfo to BookResponse
     */
    private BookResponse convertGoogleBookToResponse(GoogleBooksResponse.VolumeInfo volumeInfo) {
        BookResponse response = new BookResponse();
        response.setTitle(volumeInfo.getTitle());

        if (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()) {
            response.setAuthor(String.join(", ", volumeInfo.getAuthors()));
        }

        response.setDescription(volumeInfo.getDescription());
        response.setPublishedDate(volumeInfo.getPublishedDate());
        response.setPageCount(volumeInfo.getPageCount());

        if (volumeInfo.getImageLinks() != null) {
            response.setThumbnailUrl(volumeInfo.getImageLinks().getThumbnail());
        }

        response.setStatus(ReadingStatus.TO_READ);

        return response;
    }

    /**
     * Convert Google Books VolumeInfo to Entity
     */
    private Book convertGoogleBookToEntity(GoogleBooksResponse.VolumeInfo volumeInfo) {
        Book book = new Book();
        book.setTitle(volumeInfo.getTitle());

        if (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()) {
            book.setAuthor(String.join(", ", volumeInfo.getAuthors()));
        }

        book.setDescription(volumeInfo.getDescription());
        book.setPublishedDate(volumeInfo.getPublishedDate());
        book.setPageCount(volumeInfo.getPageCount());

        if (volumeInfo.getImageLinks() != null) {
            book.setThumbnail(volumeInfo.getImageLinks().getThumbnail());
        }

        book.setStatus(ReadingStatus.TO_READ);

        return book;
    }

    @Override
    public MultiSourceSearchResponse searchAllSources(String query) {
        log.info("Searching all sources for query: {}", query);

        List<BookSearchResult> allResults = new ArrayList<>();

        // Determine if query is ISBN or title
        boolean isIsbn = query.replaceAll("[\\s\\-]", "").matches("^\\d{10}(\\d{3})?$");

        if (isIsbn) {
            String cleanIsbn = query.replaceAll("[\\s\\-]", "");

            //Search OpenLibrary
            openLibraryService.searchByIsbn(cleanIsbn).ifPresent(data -> {
                allResults.add(convertOpenLibraryToSearchResult(data, cleanIsbn));
            });

            //Search Google Books
            try {
                googleBooksService.searchByIsbn(cleanIsbn).ifPresent(item -> {
                    allResults.add(convertGoogleBookToSearchResult(item.getVolumeInfo(), cleanIsbn));
                });
            } catch (Exception e) {
                log.warn("Google Books search failed: {}", e.getMessage());
            }
        } else {
            //Search by title

            //Search Google Books
            try {
                googleBooksService.searchByTitle(query).ifPresent(item -> {
                    allResults.add(convertGoogleBookToSearchResult(item.getVolumeInfo(), null));
                });
            } catch (Exception e) {
                log.warn("Google Books search failed: {}", e.getMessage());
            }

            //Search Jikan for manga
            jikanService.searchMangaByTitle(query).ifPresent(manga -> {
                allResults.add(convertJikanToSearchResult(manga));
            });
        }

        return MultiSourceSearchResponse.builder()
                .query(query)
                .results(allResults)
                .totalResults(allResults.size())
                .build();
    }

    // Helper converters
    private BookSearchResult convertOpenLibraryToSearchResult(Map<String, Object> data, String isbn) {
        String title = data.containsKey("title") ? (String) data.get("title") : "Unknown";
        String author = "Unknown";

        if (data.containsKey("authors")) {
            @SuppressWarnings("unchecked")
                    List<Map<String, Object>> authors = (List<Map<String, Object>>) data.get("authors");
            if (authors != null && !authors.isEmpty()) {
                author = authors.stream()
                        .map(a -> (String) a.get("name"))
                        .collect(Collectors.joining(", "));
            }
        }

        String thumbnail = null;
        if (data.containsKey("cover")) {
            @SuppressWarnings("unchecked")
                    Map<String, String> cover = (Map<String, String>) data.get("cover");
            if (cover != null) {
                thumbnail = cover.get("medium");
            }
        }

        return BookSearchResult.builder()
                .source("openLibrary")
                .title(title)
                .author(author)
                .description(data.containsKey("notes") ? (String) data.get("notes") : null)
                .thumbnailUrl(thumbnail)
                .publishedDate(data.containsKey("publish_date") ? (String) data.get("publish_date") : null)
                .pageCount(data.containsKey("number_of_pages") ? (Integer) data.get("number_of_pages") : null)
                .isbn(isbn)
                .build();
    }

    private BookSearchResult convertGoogleBookToSearchResult(GoogleBooksResponse.VolumeInfo volumeInfo, String isbn) {
        return BookSearchResult.builder()
                .source("google")
                .title(volumeInfo.getTitle())
                .author(volumeInfo.getAuthors() != null ? String.join(", ", volumeInfo.getAuthors()) : null)
                .description(volumeInfo.getDescription())
                .thumbnailUrl(volumeInfo.getImageLinks() != null ? volumeInfo.getImageLinks().getThumbnail() : null)
                .publishedDate(volumeInfo.getPublishedDate())
                .pageCount(volumeInfo.getPageCount())
                .isbn(isbn)
                .build();
    }

    private BookSearchResult convertJikanToSearchResult(JikanMangaResponse.JikanMangaData manga) {
        String author = null;
        if (manga.getAuthors() != null && !manga.getAuthors().isEmpty()) {
            author = manga.getAuthors().get(0).getName();
        }

        String thumbnail = null;
        if (manga.getImages() != null && manga.getImages().getJpg() != null) {
            thumbnail = manga.getImages().getJpg().getLargeImageUrl() != null
                    ? manga.getImages().getJpg().getLargeImageUrl()
                    : manga.getImages().getJpg().getImageUrl();
        }

        String publishedDate = null;
        if (manga.getPublished() != null && manga.getPublished().getFrom() != null) {
            publishedDate = manga.getPublished().getFrom();
            if (publishedDate.length() >= 10) {
                publishedDate = publishedDate.substring(0, 10);
            }
        }

        return BookSearchResult.builder()
                .source("jikan")
                .title(manga.getTitleEnglish() != null ? manga.getTitleEnglish() : manga.getTitle())
                .author(author)
                .description(manga.getSynopsis())
                .thumbnailUrl(thumbnail)
                .publishedDate(publishedDate)
                .pageCount(manga.getChapters() != null ? manga.getChapters() * 20 : null)
                .isbn("MAL-" + manga.getMalId())
                .sourceId(manga.getMalId().toString())
                .build();
    }
}
