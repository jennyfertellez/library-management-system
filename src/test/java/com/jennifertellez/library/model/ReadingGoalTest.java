package com.jennifertellez.library.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reading Goal Tests")
class ReadingGoalTest {

    @Test
    void testIsCurrent_whenDateIsWithinRange_returnsTrue() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now().minusDays(10));
        readingGoal.setEndDate(LocalDate.now().plusDays(10));

        boolean isCurrent = readingGoal.isCurrent();

        assertTrue(isCurrent, "Goal shuouls be current when today is within range");
    }

    @Test
    void testIsCurrent_whenBeforeStartDate_returnsFalse() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now().plusDays(10));
        readingGoal.setEndDate(LocalDate.now().plusDays(10));

        boolean isCurrent = readingGoal.isCurrent();

        assertFalse(isCurrent, "Goal shuouls be current when before start date");
    }

    @Test
    void testIsCurrent_whenAfterEndDate_returnsFalse() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now().minusDays(10));
        readingGoal.setEndDate(LocalDate.now().minusDays(10));

        boolean isCurrent = readingGoal.isCurrent();

        assertFalse(isCurrent, "Goal should not be current when after end date");
    }

    @Test
    void testGetDaysRemaining_whenGoalInFuture_returnCorrectDays() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now());
        readingGoal.setEndDate(LocalDate.now().plusDays(30));

        long daysRemaining = readingGoal.getDaysRemaining();

        assertEquals(30, daysRemaining, "Should calculate 30 days remaining");
    }

    @Test
    void testGetDaysRemaining_whenGoalExpired_returnsZero() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now().minusDays(30));
        readingGoal.setEndDate(LocalDate.now().minusDays(1));

        long daysRemaining = readingGoal.getDaysRemaining();

        assertEquals(0, daysRemaining, "Should return 0 when goal is expired");
    }

    @Test
    void testGetDaysElapsed_whenGoalStarted_returnsCorrectDays() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now().minusDays(15));
        readingGoal.setEndDate(LocalDate.now().plusDays(15));

        long daysElapsed = readingGoal.getDaysElapsed();

        assertEquals(15, daysElapsed, "Should calculate 15 days elapsed");
    }

    @Test
    void testGetdaysElapsed_whenGoalNotStarted_returnsZero() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now().plusDays(10));
        readingGoal.setEndDate(LocalDate.now().plusDays(20));

        long daysElapsed = readingGoal.getDaysElapsed();

        assertEquals(0, daysElapsed, "Should return 0 when goal is not started");
    }

    @Test
    void testGetDaysElapsed_whenGoalExpired_returnsTotalDays() {
        ReadingGoal readingGoal = new ReadingGoal();
        readingGoal.setStartDate(LocalDate.now().minusDays(40));
        readingGoal.setEndDate(LocalDate.now().minusDays(10));

        long daysElapsed = readingGoal.getDaysElapsed();

        assertEquals(30, daysElapsed, "Should return total duration when expired");
    }
}