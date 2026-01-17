package com.jennifertellez.library.integration;

import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookPaginationIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        for (int i = 1; i <= 25; i++) {
            Book book = new Book();
            book.setTitle("Book " + i);
            book.setAuthor("Author " + (i % 5));
            book.setStatus(ReadingStatus.TO_READ);
            bookRepository.save(book);
        }
    }

    @Test
    void testPagination() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Book> page = bookRepository.findAll(pageRequest);

        assertEquals(10, page.getContent().size());
        assertEquals(25, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertTrue(page.isFirst());
        assertFalse(page.isLast());
    }

    @Test
    void testSorting() {
        PageRequest pageRequest = PageRequest.of(0, 10,
                Sort.by("Title").descending());

        Page<Book> page = bookRepository.findAll(pageRequest);

        assertEquals("Book 9", page.getContent().get(0).getTitle());
    }

    @Test
    void testFilterByStatus() {
        PageRequest pageRequest = PageRequest.of(0,10);
        Page<Book> page = bookRepository.findByStatus(ReadingStatus.TO_READ, pageRequest);

        assertEquals(10, page.getContent().size());
        assertEquals(25, page.getTotalElements());
    }
}
