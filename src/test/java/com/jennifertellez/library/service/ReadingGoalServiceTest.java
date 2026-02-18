package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.CreateReadingGoalRequest;
import com.jennifertellez.library.dto.GoalProgressDTO;
import com.jennifertellez.library.dto.ReadingGoalDTO;
import com.jennifertellez.library.dto.UpdateReadingGoalRequest;
import com.jennifertellez.library.exception.ResourceNotFoundException;
import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingGoal;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.repository.BookRepository;
import com.jennifertellez.library.repository.ReadingGoalRepository;
import lombok.Locked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReadingGoal Service Tests")
class ReadingGoalServiceTest {

    @Mock
    private ReadingGoalRepository readingGoalRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReadingGoalService readingGoalService;

    private CreateReadingGoalRequest createReadingGoalRequest;
    private ReadingGoal readingGoal;

    @BeforeEach
    void setUp() {
        createReadingGoalRequest = new CreateReadingGoalRequest();
        createReadingGoalRequest.setTargetBooks(52);
        createReadingGoalRequest.setYear(2026);
        createReadingGoalRequest.setStartDate(LocalDate.of(2026, 1,1));
        createReadingGoalRequest.setEndDate(LocalDate.of(2026, 12,31));
        createReadingGoalRequest.setDescription("Read 52 books in 2026");

        readingGoal = new ReadingGoal();
        readingGoal.setId(1L);
        readingGoal.setTargetBooks(52);
        readingGoal.setYear(2026);
        readingGoal.setStartDate(LocalDate.of(2026, 1,1));
        readingGoal.setEndDate(LocalDate.of(2026, 12,31));
        readingGoal.setDescription("Read 52 books in 2026");
        readingGoal.setIsActive(true);
    }
/*
Update tests to new logic
 */
//    @Test
//    void testCreateGoal_whenActiveGoalExists_deactivatesExisting() {
//        ReadingGoal existingReadingGoal = new ReadingGoal();
//        existingReadingGoal.setId(2L);
//        existingReadingGoal.setIsActive(true);
//
//        when(readingGoalRepository.existsOverlappingGoal(anyLong(), any(), any()))
//                .thenReturn(false);
//        when(readingGoalRepository.findByIsActiveTrue())
//                .thenReturn(Optional.of(existingReadingGoal));
//        when(readingGoalRepository.save(any(ReadingGoal.class)))
//                .thenReturn(readingGoal);
//
//        readingGoalService.createGoal(createReadingGoalRequest);
//
//        assertFalse(existingReadingGoal.getIsActive(), "Existing goal should be deactivated");
//        verify(readingGoalRepository, times(2)).save(any(ReadingGoal.class));
//    }

    @Test
    void testGetAllGoals_returnsAllGoalsOrderedByDate() {
        List<ReadingGoal> readingGoals = List.of(readingGoal);
        when(readingGoalRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(readingGoals);

        List<ReadingGoal> result = readingGoalService.getAllGoals();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(readingGoalRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void testGetGoalById_whenGoalExists_returnsGoal() {
        when(readingGoalRepository.findById(1L))
                .thenReturn(Optional.of(readingGoal));

        ReadingGoal result = readingGoalService.getGoalById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(52, result.getTargetBooks());
    }

    @Test
    void testGetGoalById_whenGoalNotExists_throwsException() {
        when(readingGoalRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> readingGoalService.getGoalById(999L)
        );
    }

    @Test
    void testGetActiveGoal_whenExists_returnsActiveGoal() {
        when(readingGoalRepository.findByIsActiveTrue())
                .thenReturn(Optional.of(readingGoal));

        ReadingGoal result = readingGoalService.getActiveGoal();

        assertNotNull(result);
        assertTrue(result.getIsActive());
    }

    @Test
    void testGetActiveGoal_whenNoActiveGoal_throwsException() {
        when(readingGoalRepository.findByIsActiveTrue())
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> readingGoalService.getActiveGoal());
    }

    @Test
    void testGetCurrentGoal_whenExists_returnsCurrentGoal() {
        when(readingGoalRepository.findCurrentGoal(any(LocalDate.class)))
                .thenReturn(Optional.of(readingGoal));

        ReadingGoal result = readingGoalService.getCurrentGoal();

        assertNotNull(result);
        verify(readingGoalRepository).findCurrentGoal(LocalDate.now());
    }

    @Test
    void testUpdateGoal_whenUpdatingTarget_updatesSuccessfully() {
        UpdateReadingGoalRequest updateReadingGoalRequest = new UpdateReadingGoalRequest();
        updateReadingGoalRequest.setTargetBooks(60);

        when(readingGoalRepository.findById(1L))
                .thenReturn(Optional.of(readingGoal));
        when(readingGoalRepository.save(any(ReadingGoal.class)))
                .thenReturn(readingGoal);

        ReadingGoal result = readingGoalService.updateGoal(1L, updateReadingGoalRequest);

        assertNotNull(result);
        verify(readingGoalRepository).save(readingGoal);
    }
/*
Update tests to new logic
 */
//    @Test
//    void testUpdateGoal_whenDatesOverlap_throwsException() {
//        UpdateReadingGoalRequest updateReadingGoalRequest = new UpdateReadingGoalRequest();
//        updateReadingGoalRequest.setStartDate(LocalDate.of(2026, 6, 1));
//        updateReadingGoalRequest.setEndDate(LocalDate.of(2026, 12, 31));
//
//        when(readingGoalRepository.findById(1L))
//                .thenReturn(Optional.of(readingGoal));
//        when(readingGoalRepository.existsOverlappingGoal(eq(1L), any(), any()))
//                .thenReturn(true);
//
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> readingGoalService.updateGoal(1L, updateReadingGoalRequest)
//        );
//
//        verify(readingGoalRepository, never()).save(any());
//    }
//
//    @Test
//    void testUpdateGoal_whenSettingActive_deactivatesOthers() {
//        ReadingGoal otherGoal = new ReadingGoal();
//        otherGoal.setId(2L);
//        otherGoal.setIsActive(true);
//
//        UpdateReadingGoalRequest updateReadingGoalRequest = new UpdateReadingGoalRequest();
//        updateReadingGoalRequest.setIsActive(true);
//
//        when(readingGoalRepository.findById(1L))
//                .thenReturn(Optional.of(readingGoal));
//        when(readingGoalRepository.findByIsActiveTrue())
//                .thenReturn(Optional.of(otherGoal));
//        when(readingGoalRepository.save(any(ReadingGoal.class)))
//                .thenReturn(readingGoal);
//
//        readingGoalService.updateGoal(1L, updateReadingGoalRequest);
//
//        assertFalse(otherGoal.getIsActive());
//        verify(readingGoalRepository, times(2)).save(any(ReadingGoal.class));
//    }

    @Test
    void testDeleteGoal_whenGoalExists_deletesSuccessfully() {
        when(readingGoalRepository.findById(1L))
                .thenReturn(Optional.of(readingGoal));
        doNothing().when(readingGoalRepository).delete(any(ReadingGoal.class));

        readingGoalService.deleteGoal(1L);

        verify(readingGoalRepository).delete(readingGoal);
    }

    @Test
    void testDeleteGoal_whenGoalNotExists_throwsException() {
        when(readingGoalRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> readingGoalService.deleteGoal(999L)
        );

        verify(readingGoalRepository, never()).delete(any());
    }

    @Test
    void testGetGoalProgress_withBooksRead_calculatesCorrectly() {
        List<Book> finishedBooks = createFinishedBooks(10);

        when(readingGoalRepository.findById(1L))
                .thenReturn(Optional.of(readingGoal));
        when(bookRepository.findByStatusAndFinishedDateBetween(
                eq(ReadingStatus.FINISHED),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(finishedBooks);

        GoalProgressDTO result = readingGoalService.getGoalProgress(1L);

        assertNotNull(result);
        assertEquals(10, result.getBooksRead());
        assertEquals(42, result.getBooksRemaining());
        assertTrue(result.getPercentageCompleted() > 0);
        assertNotNull(result.getBooksPerMonth());
        assertNotNull(result.getOnTrack());
    }

    @Test
    void testGetGoalProgress_withNoBooksRead_returnsZeroProgress() {
        when(readingGoalRepository.findById(1L))
                .thenReturn(Optional.of(readingGoal));
        when(bookRepository.findByStatusAndFinishedDateBetween(
                any(),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(new ArrayList<>());

        GoalProgressDTO result = readingGoalService.getGoalProgress(1L);

        assertNotNull(result);
        assertEquals(0, result.getBooksRead());
        assertEquals(52, result.getBooksRemaining());
        assertEquals(0.0, result.getPercentageCompleted());
    }

    @Test
    void testGetGoalProgress_whenOnPace_identifiesOnTrack() {
        ReadingGoal activeGoal = new ReadingGoal();
        activeGoal.setId(1L);
        activeGoal.setTargetBooks(12);
        activeGoal.setStartDate(LocalDate.now().minusDays(30));
        activeGoal.setEndDate(LocalDate.now().plusDays(335));
        activeGoal.setIsActive(true);

        List<Book> finishedBooks = createFinishedBooks(1);

        when(readingGoalRepository.findById(1L))
                .thenReturn(Optional.of(activeGoal));
        when(bookRepository.findByStatusAndFinishedDateBetween(
                any(),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(finishedBooks);

        GoalProgressDTO result = readingGoalService.getGoalProgress(1L);

        assertNotNull(result);
        assertTrue(result.getOnTrack(), "User should be on track");
    }

    @Test
    void testGetGoalProgress_includesRecentlyFinishedBooks() {
        List<Book> finishedBooks = createFinishedBooks(10);

        when(readingGoalRepository.findById(1L))
                .thenReturn(Optional.of(readingGoal));
        when(bookRepository.findByStatusAndFinishedDateBetween(
                any(),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(finishedBooks);

        GoalProgressDTO result = readingGoalService.getGoalProgress(1L);

        assertNotNull(result.getRecentlyFinished());
        assertTrue(result.getRecentlyFinished().size() <= 5, "Should limit to 5 recent books");
    }

    private List<Book> createFinishedBooks(int count) {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Book book = new Book();
            book.setId((long) i);
            book.setTitle("Book " + i);
            book.setAuthor("Author " + i);
            book.setStatus(ReadingStatus.FINISHED);
            book.setFinishedDate(LocalDate.now().minusDays(i));
            books.add(book);
        }
        return books;
    }
}