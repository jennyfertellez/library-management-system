package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.BookResponse;
import com.jennifertellez.library.dto.CreateBookRequest;
import com.jennifertellez.library.exception.BookNotFoundException;
import com.jennifertellez.library.exception.DuplicateBookException;
import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private CreateBookRequest createRequest;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setIsbn("1234567890");
        testBook.setStatus(ReadingStatus.TO_READ);

        createRequest = new CreateBookRequest();
        createRequest.setTitle("Test Book");
        createRequest.setAuthor("Test Author");
        createRequest.setIsbn("1234567890");
        createRequest.setStatus(ReadingStatus.TO_READ);
    }

    @Test
    void createBook_Success() {
        //Arrange
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        //Act
        BookResponse response = bookService.createBook(createRequest);

        //Assert
        assertNotNull(response);
        assertEquals("Test Book", response.getTitle());
        assertEquals("Test Author", response.getAuthor());
        verify(bookRepository).existsByIsbn("1234567890");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_DuplicatedIsbn_ThrowsException() {
        //Arrange
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        //Act and Assert
        assertThrows(DuplicateBookException.class, () -> {
            bookService.createBook(createRequest);
        });

        verify(bookRepository).existsByIsbn("1234567890");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getBookById_Success() {
        //Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        //Act
        BookResponse response = bookService.getBookById(1L);

        //Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Book", response.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_NotFound_ThrowsException() {
        //Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(BookNotFoundException.class, () -> {
            bookService.getBookById(999L);
        });

        verify(bookRepository).findById(999L);
    }

    @Test
    void getAllBooks_Success() {
        //Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        //Act
        List<BookResponse> responses = bookService.getAllBooks();

        //Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Book", responses.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    void searchBooks_Success() {
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.searchBooks("test")).thenReturn(books);

        List<BookResponse> responses = bookService.searchBooks("test");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(bookRepository).searchBooks("test");
    }

    @Test
    void deleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_NotFound_ThrowsException() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBook(999L);
        });

        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(anyLong());
    }
}
