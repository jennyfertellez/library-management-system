package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.*;
import com.jennifertellez.library.exception.ResourceNotFoundException;
import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingGoal;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.repository.BookRepository;
import com.jennifertellez.library.repository.ReadingGoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReadingGoalService {

    private final ReadingGoalRepository readingGoalRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ReadingGoal createGoal(CreateReadingGoalRequest createReadingGoalRequest) {
        log.info("Creating new reading goal for year {}", createReadingGoalRequest.getYear());

        // Check for overlapping goals
        boolean hasOverlap = readingGoalRepository.existsOverlappingGoal(
                0L,
                createReadingGoalRequest.getStartDate(),
                createReadingGoalRequest.getEndDate()
        );

        if (hasOverlap) {
            throw new IllegalArgumentException(
                    "A goal already exists for this date range"
            );
        }

        // Deactivate other active goals if this one is being set as active
        readingGoalRepository.findByIsActiveTrue().ifPresent(existingGoal -> {
            existingGoal.setIsActive(false);
            readingGoalRepository.save(existingGoal);
            log.info("Deactivated previous active goal: {}", existingGoal.getId());
        });

        ReadingGoal goal = new ReadingGoal();
        goal.setTargetBooks(createReadingGoalRequest.getTargetBooks());
        goal.setYear(createReadingGoalRequest.getYear());
        goal.setStartDate(createReadingGoalRequest.getStartDate());
        goal.setEndDate(createReadingGoalRequest.getEndDate());
        goal.setDescription(createReadingGoalRequest.getDescription());
        goal.setIsActive(true);

        ReadingGoal saved = readingGoalRepository.save(goal);
        log.info("Created reading goal with ID: {}", saved.getId());

        return saved;
    }

    public List<ReadingGoal> getAllGoals() {
        return readingGoalRepository.findAllByOrderByCreatedAtDesc();
    }

    public ReadingGoal getGoalById(Long id) {
        return readingGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reading goal not found for id: " + id));
    }

    public ReadingGoal getActiveGoal() {
        return readingGoalRepository.findByIsActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No active reading goal found"));
    }

    public ReadingGoal getCurrentGoal() {
        return readingGoalRepository.findCurrentGoal(LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("Reading goal not found for current date"));
    }

    @Transactional
    public ReadingGoal updateGoal(Long id, UpdateReadingGoalRequest updateReadingGoalRequest) {
        log.info("Updating reading goal for id {}", id);

        ReadingGoal readingGoal = getGoalById(id);

        if (updateReadingGoalRequest.getTargetBooks() != null) {
            readingGoal.setTargetBooks(updateReadingGoalRequest.getTargetBooks());
        }
        if (updateReadingGoalRequest.getStartDate() != null) {
            readingGoal.setStartDate(updateReadingGoalRequest.getStartDate());
        }
        if (updateReadingGoalRequest.getEndDate() != null) {
            readingGoal.setEndDate(updateReadingGoalRequest.getEndDate());
        }
        if (updateReadingGoalRequest.getDescription() != null) {
            readingGoal.setDescription(updateReadingGoalRequest.getDescription());
        }
        if (updateReadingGoalRequest.getIsActive() != null) {
            readingGoal.setIsActive(updateReadingGoalRequest.getIsActive());
        }

        ReadingGoal updated = readingGoalRepository.save(readingGoal);
        log.info("Updated reading goal: {}", id);

        return updated;
    }

    @Transactional
    public void deleteGoal(Long id) {
        log.info("Deleting reading goal: {}", id);

        ReadingGoal goal = getGoalById(id);
        readingGoalRepository.delete(goal);

        log.info("Deleted reading goal: {}", id);
    }

    public GoalProgressDTO getGoalProgress(Long goalId) {
        ReadingGoal goal = getGoalById(goalId);

        List<Book> finishedBooks = bookRepository.findByStatusAndFinishedDateBetween(
                ReadingStatus.FINISHED,
                goal.getStartDate(),
                goal.getEndDate()
        );

        int booksRead = finishedBooks.size();
        int booksRemaining = Math.max(0, goal.getTargetBooks() - booksRead);
        double percentageComplete = goal.getTargetBooks() > 0
                ? (booksRead * 100.0) / goal.getTargetBooks() : 0.0;

        long daysElapsed = goal.getDaysElapsed();
        long daysRemaining = goal.getDaysRemaining();
        long totalDays = ChronoUnit.DAYS.between(
                goal.getStartDate(),
                goal.getEndDate()
        );

        // Books per month (target pace)
        double monthsInGoal = totalDays / 30.0;
        double booksPerMonth = monthsInGoal > 0 ?
                goal.getTargetBooks() / monthsInGoal : 0.0;

        // Books per week (target pace)
        double weeksInGoal = totalDays / 7.0;
        double booksPerWeek = weeksInGoal > 0 ?
                goal.getTargetBooks() / weeksInGoal : 0.0;

        // Actual pace thus far
        double monthsElapsed = daysElapsed / 30.0;
        double averageBooksPerMonth = monthsElapsed > 0 ?
                booksRead / monthsElapsed : 0.0;

        // Determine if on track
        double expectedBooks = daysElapsed > 0 ?
                (goal.getTargetBooks() * daysElapsed * 1.0) / totalDays : 0.0;
        boolean onTrack = booksRead >= expectedBooks * 0.9; // 90% threshold


        List<Book> recentlyFinished = finishedBooks.stream()
                .sorted((b1, b2) -> b2.getFinishedDate().compareTo(b1.getFinishedDate()))
                .limit(5)
                .toList();

        GoalProgressDTO progress = GoalProgressDTO.fromGoal(goal);
        progress.setBooksRead(booksRead);
        progress.setBooksRemaining(booksRemaining);
        progress.setPercentageCompleted(Math.round(percentageComplete * 10.0) / 10.0);
        progress.setBooksPerMonth(Math.round(booksPerMonth * 10.0) / 10.0);
        progress.setBooksPerWeek(Math.round(booksPerWeek * 10.0) / 10.0);
        progress.setAverageBooksPerMonth(Math.round(averageBooksPerMonth * 10.0) / 10.0);
        progress.setOnTrack(onTrack);
        progress.setRecentlyFinished(recentlyFinished);

        return progress;
    }
}
