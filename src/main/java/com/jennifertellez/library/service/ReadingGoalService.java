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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingGoalService {

    private final ReadingGoalRepository readingGoalRepository;
    private final BookRepository bookRepository;

    @Transactional
    public ReadingGoalDTO createGoal(CreateReadingGoalRequest createReadingGoalRequest) {
        ReadingGoal goal = new ReadingGoal();
        goal.setTargetBooks(createReadingGoalRequest.getTargetBooks());
        goal.setYear(createReadingGoalRequest.getYear());
        goal.setStartDate(createReadingGoalRequest.getStartDate());
        goal.setEndDate(createReadingGoalRequest.getEndDate());
        goal.setDescription(createReadingGoalRequest.getDescription());

        ReadingGoal saved = readingGoalRepository.save(goal);
        return covertToDTO(saved);
    }

    public List<ReadingGoalDTO> getAllGoals() {
        return readingGoalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReadingGoalDTO getGoalById(Long id) {
        ReadingGoal goal = readingGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reading goal not found with id: " + id));
        return convertToDTO(goal);
    }

    public ReadingGoalDTO getCurrentGoal() {
        ReadingGoal goal = readingGoalRepository.findActiveGoalByDate(LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active reading goal found"));
        return convertToDTO(goal);
    }

    public List<ReadingGoalDTO> getCurrentAndFutureGoals() {
        return readingGoalRepository.findCurrentAndFutureGoals(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReadingGoalDTO updateGoal(Long id, UpdateReadingGoalRequest updateReadingGoalRequest) {
        ReadingGoal readingGoal = readingGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reading goal not found with id: " + id));

        if (updateReadingGoalRequest.getTargetBooks() != null) {
            readingGoal.setTargetBooks(updateReadingGoalRequest.getTargetBooks());
        }
        if (updateReadingGoalRequest.getYear() != null) {
            readingGoal.setYear(updateReadingGoalRequest.getYear());
        }
        if (updateReadingGoalRequest.getStartDate() != null) {
            readingGoal.setStartDate(updateReadingGoalRequest.getStartDate());
        }
        if (updateReadingGoalRequest.getEndDate() != null) {
            readingGoal.setEndDate(updateReadingGoalRequest.getEndDate());
        }
        if (updateReadingGoalRequest.getDescription() != null) {
            readingGoal.setDescription(readingGoal.getDescription());
        }

        ReadingGoal updated = readingGoalRepository.save(readingGoal);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteGoal(Long id) {
        if (!readingGoalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reading goal not found with id: " + id);
        }
        readingGoalRepository.deleteById(id);
    }

    public GoalProgressDTO getGoalProgress(Long id) {
        ReadingGoal goal = readingGoalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reading goal not found with id: " + id));

        List<Book> finishedBooks = bookRepository.findByStatusAndFinishedDateBetween(
                ReadingStatus.FINISHED,
                goal.getStartDate(),
                goal.getEndDate()
        );

        int booksRead = finishedBooks.size();
        int booksRemaining = Math.max(0, goal.getTargetBooks() - booksRead);
        double percentageComplete = goal.getTargetBooks() > 0
                ? ((double) booksRead / goal.getTargetBooks()) * 100
                : 0;

        LocalDate today = LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(goal.getStartDate(), goal.getEndDate()) + 1;
        long daysRemaining = goal.getEndDate().isBefore(today)
                ? 0
                : ChronoUnit.DAYS.between(today, goal.getEndDate()) + 1;

        // Calculate reading pace
        double totalMonths = totalDays / 30.0;
        double totalWeeks = totalDays / 7.0;
        double booksPerMonth = totalMonths > 0 ? goal.getTargetBooks() / totalMonths : 0;
        double booksPerWeek = totalWeeks > 0 ? goal.getTargetBooks() / totalWeeks : 0;

        // Calculate if on track
        long daysPassed = ChronoUnit.DAYS.between(goal.getStartDate(), today);
        double expectedProgress = totalDays > 0 ? ((double) daysPassed / totalDays) * 100 : 0;
        boolean onTrack = percentageComplete >= expectedProgress || goal.getEndDate().isBefore(today);

        // Get recently finished books (last 5)
        List<BookDTO> recentBooks = finishedBooks.stream()
                .sorted((b1, b2) -> b2.getFinishedDate().compareTo(b1.getFinishedDate()))
                .limit(5)
                .map(this::convertBookToDTO)
                .collect(Collectors.toList());

        GoalProgressDTO progress = new GoalProgressDTO();
        progress.setGoalId(goal.getId());
        progress.setTargetBooks(goal.getTargetBooks());
        progress.setBooksRead(booksRead);
        progress.setBooksRemaining(booksRemaining);
        progress.setPercentageComplete(Math.round(percentageComplete * 10.0) / 10.0);
        progress.setStartDate(goal.getStartDate());
        progress.setEndDate(goal.getEndDate());
        progress.setDaysRemaining((int) daysRemaining);
        progress.setTotalDays((int) totalDays);
        progress.setBooksPerMonth(Math.round(booksPerMonth * 10.0) / 10.0);
        progress.setBooksPerWeek(Math.round(booksPerWeek * 10.0) / 10.0);
        progress.setOnTrack(onTrack);
        progress.setRecentlyFinishedBooks(recentBooks);

        return progress;
    }

    private ReadingGoalDTO convertToDTO(ReadingGoal goal) {
        ReadingGoalDTO dto = new ReadingGoalDTO();
        dto.setId(goal.getId());
        dto.setTargetBooks(goal.getTargetBooks());
        dto.setYear(goal.getYear());
        dto.setStartDate(goal.getStartDate());
        dto.setEndDate(goal.getEndDate());
        dto.setDescription(goal.getDescription());
        dto.setCreatedAt(goal.getCreatedAt());
        dto.setUpdatedAt(goal.getUpdatedAt());
        return dto;
    }

    private BookDTO convertBookToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setThumbnailUrl(book.getThumbnailUrl());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setPageCount(book.getPageCount());
        dto.setStatus(book.getStatus());
        dto.setRating(book.getRating());
        dto.setNotes(book.getNotes());
        dto.setFinishedDate(book.getFinishedDate());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        return dto;
    }
}
