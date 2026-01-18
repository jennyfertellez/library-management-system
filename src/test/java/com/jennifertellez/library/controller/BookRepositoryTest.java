package com.jennifertellez.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jennifertellez.library.dto.BookResponse;
import com.jennifertellez.library.dto.CreateBookRequest;
import com.jennifertellez.library.dto.PageResponse;
import com.jennifertellez.library.exception.BookNotFoundException;
import com.jennifertellez.library.exception.DuplicateBookException;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.service.BookService;
import com.jennifertellez.library.service.GoogleBooksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private GoogleBooksService googleBooksService;

    private BookResponse bookResponse;
    private CreateBookRequest createBookRequest;

    @BeforeEach
    void setup() {
        bookResponse = new BookResponse();
        bookResponse.setId(1L);
        bookResponse.setTitle("Test Book");
        bookResponse.setAuthor("Test Author");
        bookResponse.setIsbn("1234567890");
        bookResponse.setStatus(ReadingStatus.TO_READ);

        createBookRequest = new CreateBookRequest();
        createBookRequest.setTitle("Test Book");
        createBookRequest.setAuthor("Test Author");
        createBookRequest.setIsbn("1234567890");
    }

    @Test
    void createBook_Success() throws Exception {
        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenReturn(bookResponse);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    void createBook_DuplicateIsbn_Return409() throws Exception {
        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenThrow(new DuplicateBookException("1234567890"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void createBook_InvalidData_Returns400() throws Exception {
        CreateBookRequest invalidRequest = new CreateBookRequest();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookById_Success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(bookResponse);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookById_NotFound_Returns404() throws Exception {
        when(bookService.getBookById(999L))
                .thenThrow(new BookNotFoundException(999L));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBooks_WithPagination() throws Exception {
        List<BookResponse> books = Arrays.asList(bookResponse);
        PageResponse<BookResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(books);
        pageResponse.setTotalElements(1L);
        pageResponse.setTotalPages(1);

        when(bookService.getAllBooks(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void searchBooks_Success() throws Exception {
        PageResponse<BookResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(Arrays.asList(bookResponse));

        when(bookService.searchBooks(anyString(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/books/search")
                        .param("term", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Book"));
    }

    @Test
    void deleteBook_Success() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}