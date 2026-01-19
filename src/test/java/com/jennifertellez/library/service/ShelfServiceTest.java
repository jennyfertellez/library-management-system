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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ShelfServiceImpl shelfService;

    private Shelf testShelf;
    private CreateShelfRequest createShelfRequest;
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

        createShelfRequest = new CreateShelfRequest();
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
    void createShelf_DuplicatedShelf_ThrowsException() {
        createShelfRequest.setName("Favorites");
        when(shelfRepository.existsByName("Favorites")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            shelfService.createShelf(createShelfRequest);
        });

        verify(shelfRepository).existsByName("Favorites");
        verify(shelfRepository, never()).save(any(Shelf.class));
    }

    @Test
    void getShelfById_Success() {
        when(shelfRepository.findById(1L)).thenReturn(Optional.of(testShelf));

        ShelfResponse response = shelfService.getShelfById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Favorites", response.getName());
        verify(shelfRepository).findById(1L);
    }

    @Test
    void getShelfId_NotFound_ThrowsException() {
        when(shelfRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            shelfService.getShelfById(999L);
        });

        verify(shelfRepository).findById(999L);
    }

    @Test
    void getAllShelves_Success() {
        List<Shelf> shelves = Arrays.asList(testShelf);
        when(shelfRepository.findAll()).thenReturn(shelves);

        List<ShelfResponse> responses = shelfService.getAllShelves();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Favorites", responses.get(0).getName());
        verify(shelfRepository).findAll();
    }

    @Test
    void deleteShelf_Success() {
        when(shelfRepository.existsById(1L)).thenReturn(true);
        doNothing().when(shelfRepository).deleteById(1L);

        shelfService.deleteShelf(1L);

        verify(shelfRepository).existsById(1L);
        verify(shelfRepository).deleteById(1L);

    }

    @Test
    void deleteShelf_NotFound_ThrowsException() {
        when(shelfRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            shelfService.deleteShelf(999L);
        });

        verify(shelfRepository).existsById(999L);
        verify(shelfRepository, never()).deleteById(anyLong());
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
