package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.BookResponse;
import com.jennifertellez.library.dto.CreateBookRequest;
import com.jennifertellez.library.dto.PageResponse;
import com.jennifertellez.library.dto.UpdateBookRequest;
import com.jennifertellez.library.model.ReadingStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    BookResponse createBook(CreateBookRequest request);

    BookResponse getBookById(Long id);

    List<BookResponse> getAllBooks();

    List<BookResponse> getBooksByStatus(ReadingStatus status);

    BookResponse updateBook(Long id, UpdateBookRequest request);

    void deleteBook(Long id);

    List<BookResponse> searchBooks(String searchTerm);

    PageResponse<BookResponse> getAllBooks(Pageable pageable);

    PageResponse<BookResponse> searchBooks( String searchTerm, Pageable pageable);

    PageResponse<BookResponse> getBooksByStatus(ReadingStatus status, Pageable pageable);

    PageResponse<BookResponse> getBooksByAuthor(String author, Pageable pageable);

}
