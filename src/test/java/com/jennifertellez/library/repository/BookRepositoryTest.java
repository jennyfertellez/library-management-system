package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void findByIsbn_Success() {
        //Arrange
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setStatus(ReadingStatus.TO_READ);
        entityManager.persist(book);
        entityManager.flush();

        //Act
        Optional<Book> found = bookRepository.findByIsbn("1234567890");

        //Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Book");

    }

    @Test
    void existsByIsbn_True() {

        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setStatus(ReadingStatus.TO_READ);
        entityManager.persist(book);
        entityManager.flush();

        boolean exists = bookRepository.existsByIsbn("1234567890");

        assertTrue(exists);
    }

    @Test
    void existsByIsbn_False() {

        boolean exists = bookRepository.existsByIsbn("9999999999");

        assertFalse(exists);
    }

    @Test
    void findByStatus_Success() {

        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setStatus(ReadingStatus.TO_READ);
        entityManager.persist(book1);

        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setStatus(ReadingStatus.CURRENTLY_READING);
        entityManager.persist(book2);

        entityManager.flush();

        List<Book> toReadBooks = bookRepository.findByStatus(ReadingStatus.TO_READ);

        assertEquals(1, toReadBooks.size());
        assertEquals("Book 1", toReadBooks.get(0).getTitle());
    }

    @Test
    void searchBooks_FindsByTitle() {

        Book book = new Book();
        book.setTitle("Heart the Lover");
        book.setAuthor("Lily King");
        book.setStatus(ReadingStatus.TO_READ);
        entityManager.persist(book);
        entityManager.flush();

        List<Book> found  = bookRepository.searchBooks("heart");

        assertEquals(1, found.size());
        assertEquals("Heart the Lover", found.get(0).getTitle());
    }

    @Test
    void searchBooks_FindsByAuthor() {

        Book book = new Book();
        book.setTitle("Heart the Lover");
        book.setAuthor("Lily King");
        book.setStatus(ReadingStatus.TO_READ);
        entityManager.persist(book);
        entityManager.flush();

        List<Book> found = bookRepository.searchBooks("king");

        assertEquals(1, found.size());
        assertEquals("Lily King", found.get(0).getAuthor());
    }


}