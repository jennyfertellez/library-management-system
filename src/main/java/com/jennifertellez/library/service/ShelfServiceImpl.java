package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.BookResponse;
import com.jennifertellez.library.dto.CreateShelfRequest;
import com.jennifertellez.library.dto.ShelfResponse;
import com.jennifertellez.library.dto.UpdateShelfRequest;
import com.jennifertellez.library.exception.BookNotFoundException;
import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.Shelf;
import com.jennifertellez.library.repository.BookRepository;
import com.jennifertellez.library.repository.ShelfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShelfServiceImpl implements ShelfService {

    private final ShelfRepository shelfRepository;
    private final BookRepository bookRepository;

    @Override
    public ShelfResponse createShelf(CreateShelfRequest request) {
        log.info("Creating new shelf: {}", request.getName());

        if (shelfRepository.existsByName(request.getName())) {
            throw new RuntimeException("Shelf with name '" + request.getName() + "' already exists");
        }

        Shelf shelf = new Shelf();
        shelf.setName(request.getName());
        shelf.setDescription(request.getDescription());

        Shelf savedShelf = shelfRepository.save(shelf);
        log.info("Shelf created with ID: {}", savedShelf.getId());

        return mapToResponse(savedShelf);
    }

    @Override
    @Transactional(readOnly = true)
    public ShelfResponse getShelfById(Long id) {
        log.info("Fetching shelf with ID: {}", id);
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found with ID: " + id));
        return mapToResponse(shelf);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShelfResponse> getAllShelves() {
        log.info("Fetching all shelves");
        return shelfRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ShelfResponse updateShelf(Long id, UpdateShelfRequest request) {
        log.info("Updating shelf with ID: {}", id);

        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found with ID: " + id));

        if (request.getName() != null && !request.getName().equals(shelf.getName())) {
            if (shelfRepository.existsByName(request.getName())) {
                throw new RuntimeException("Shelf with name '" + request.getName() + "' already exists");
            }
            shelf.setName(request.getName());
        }

        Shelf updatedShelf = shelfRepository.save(shelf);
        log.info("Shelf updated with ID: {}", updatedShelf.getId());

        return mapToResponse(updatedShelf);
    }

    @Override
    public void deleteShelf(Long id) {
        log.info("Deleting shelf with ID: {}", id);

        if (!shelfRepository.existsById(id)) {
            throw new RuntimeException("Shelf not found with ID: " + id);
        }

        shelfRepository.deleteById(id);
        log.info("Shelf deleted with ID: {}", id);
    }

    @Override
    public ShelfResponse addBookToShelf(Long shelfId, Long bookId) {
        log.info("Adding book {} to shelf {}", bookId, shelfId);

        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new RuntimeException("Shelf not found with ID: " + shelfId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (shelf.getBooks() == null) {
            shelf.setBooks(new HashSet<>());
        }
        if (book.getShelves() == null) {
            book.setShelves(new HashSet<>());
        }

        if (shelf.getBooks().contains(book)) {
            log.info("Book {} already in shelf {}", bookId, shelfId);
            return mapToResponse(shelf);
        }

        shelf.getBooks().add(book);
        book.getShelves().add(shelf);

        bookRepository.save(book);
        Shelf savedShelf = shelfRepository.save(shelf);

        log.info("Successfully added book {} to shelf {}", bookId, shelfId);
        return mapToResponse(savedShelf);
    }

    @Override
    public ShelfResponse removeBookFromShelf(Long shelfId, Long bookId) {
        log.info("Removing book {} from shelf {}", bookId, shelfId);

        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new RuntimeException("Shelf not found with ID: " + shelfId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        shelf.removeBook(book);
        shelfRepository.save(shelf);

        log.info("Book {} removed from shelf {}", bookId, shelfId);
        return mapToResponse(shelf);
    }

    private ShelfResponse mapToResponse(Shelf shelf) {
        ShelfResponse response = new ShelfResponse();
        response.setId(shelf.getId());
        response.setName(shelf.getName());
        response.setDescription(shelf.getDescription());
        response.setBookCount(shelf.getBooks().size());
        response.setCreatedAt(shelf.getCreatedAt());

        if (shelf.getBooks() == null) {
            response.setBookCount(0);
            response.setBooks(new ArrayList<>());
            return response;
        }

        List<BookResponse> bookResponses = new ArrayList<>();
        for (Book book : shelf.getBooks()) {
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(book.getId());
            bookResponse.setTitle(book.getTitle());
            bookResponse.setAuthor(book.getAuthor());
            bookResponse.setIsbn(book.getIsbn());
            bookResponse.setStatus(book.getStatus());
            bookResponse.setRating(book.getRating());
            bookResponse.setDescription(book.getDescription());
            bookResponse.setPublishedDate(book.getPublishedDate());
            bookResponse.setPageCount(book.getPageCount());
            bookResponse.setThumbnailUrl(book.getThumbnail());
            bookResponse.setFinishedDate(book.getFinishedDate());
            bookResponse.setNotes(book.getNotes());
            bookResponse.setCreatedAt(book.getCreatedAt());
            bookResponse.setUpdatedAt(book.getUpdatedAt());
            bookResponses.add(bookResponse);
        }

        response.setBooks(bookResponses);
        return response;
    }
}