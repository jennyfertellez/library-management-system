package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.CreateShelfRequest;
import com.jennifertellez.library.dto.ShelfResponse;
import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.model.Shelf;
import com.jennifertellez.library.repository.BookRepository;
import com.jennifertellez.library.repository.ShelfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    private Shelf testShelf;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testShelf = new Shelf();
        testShelf.setId(1L);
        testShelf.setName("Favorites");
        testShelf.setDescription("My favorite books");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("TestBook");
        testBook.setStatus(ReadingStatus.TO_READ);
    }

    @Test
    void createShelf_Success() {
        CreateShelfRequest request = new CreateShelfRequest();
        request.setName("Favorites");
        request.setDescription("My favorite books");

        when(shelfRepository.existsByName(anyString())).thenReturn(false);
        when(shelfRepository.save(any(Shelf.class))).thenReturn(testShelf);

        ShelfResponse response = shelfService.createShelf(request);

        assertNotNull(response);
        assertEquals("Favorites", response.getName());
        verify(shelfRepository).save(any(Shelf.class));
    }

    @Test
    void addBookToShelf_Success() {
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(testShelf));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(shelfRepository.save(any(Shelf.class))).thenReturn(testShelf);

        ShelfResponse response = shelfService.addBookToShelf(1L, 1L);

        assertNotNull(response);
        verify(shelfRepository).save(any(Shelf.class));
    }
}
