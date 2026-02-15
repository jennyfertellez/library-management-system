package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.ReadingGoal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("ReadingGoal Repository Tests")
class ReadingGoalRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ReadingGoalRepository readingGoalRepository;

    private ReadingGoal activeReadingGoal;
    private ReadingGoal inactiveReadingGoal;

    @BeforeEach
    void setUp() {
        activeReadingGoal = new ReadingGoal();
        activeReadingGoal.setTargetBooks(52);
        activeReadingGoal.setYear(2026);
        activeReadingGoal.setStartDate(LocalDate.of(2026, 1, 1));
        activeReadingGoal.setEndDate(LocalDate.of(2026, 12, 31));
        activeReadingGoal.setIsActive(true);
        testEntityManager.persist(activeReadingGoal);

        inactiveReadingGoal = new ReadingGoal();
        inactiveReadingGoal.setTargetBooks(24);
        inactiveReadingGoal.setYear(2025);
        inactiveReadingGoal.setStartDate(LocalDate.of(2025, 1, 1));
        inactiveReadingGoal.setEndDate(LocalDate.of(2025, 12, 31));
        inactiveReadingGoal.setIsActive(false);
        testEntityManager.persist(inactiveReadingGoal);

        testEntityManager.flush();
    }

    @Test
    void testFindByIsActiveTrue_returnsActiveGoal() {
        Optional<ReadingGoal> result = readingGoalRepository.findByIsActiveTrue();

        assertTrue(result.isPresent());
        assertEquals(52, result.get().getTargetBooks());
        assertTrue(result.get().getIsActive());
    }

    @Test
    void testExistsOverlappingGoal_whenOverlapExists_returnsTrue() {
        LocalDate newStartDate = LocalDate.of(2026, 6, 1);
        LocalDate newEndDate = LocalDate.of(2027, 6, 1);

        boolean exists = readingGoalRepository.existsOverlappingGoal(0L, newStartDate, newEndDate);

        assertTrue(exists, "Should detect overlap with existing 2026 goal");
    }

    @Test
    void testExistsOverlappingGoal_whenNoOverlapExists_returnsFalse() {
        LocalDate newStartDate = LocalDate.of(2027, 1 ,1);
        LocalDate newEndDate = LocalDate.of(2027, 12, 31);

        boolean exists = readingGoalRepository.existsOverlappingGoal(0L, newStartDate, newEndDate);

        assertFalse(exists, "Should not detect overlap for 2027 dates");
    }

    @Test
    void testExistsOverlappingGoal_excludesSpecifiedGoal() {
        Long excludeId = activeReadingGoal.getId();
        LocalDate sameStartDate = activeReadingGoal.getStartDate();
        LocalDate sameEndDate = activeReadingGoal.getEndDate();

        boolean exists = readingGoalRepository.existsOverlappingGoal(excludeId, sameStartDate, sameEndDate);

        assertFalse(exists, "Should not detect overlap when excluding the same goal");
    }

    @Test
    void testFindCurrentGoal_whenDateWithinRange_returnsGoal() {
        LocalDate dateInRange = LocalDate.of(2026, 6, 15);

        Optional<ReadingGoal> result = readingGoalRepository.findCurrentGoal(dateInRange);

        assertTrue(result.isPresent());
        assertEquals(2026, result.get().getYear());
    }

    @Test
    void testFindCurrentGoal_whenDateOutsideRange_returnsEmpty() {
        LocalDate dateOutside = LocalDate.of(2024, 1, 1);

        Optional<ReadingGoal> result = readingGoalRepository.findCurrentGoal(dateOutside);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllByOrderByCreatedAtDesc_returnsOrderedGoals() {
        List<ReadingGoal> result = readingGoalRepository.findAllByOrderByCreatedAtDesc();

        assertEquals(2, result.size());
        assertEquals(inactiveReadingGoal.getId(), result.get(0).getId());
    }

}