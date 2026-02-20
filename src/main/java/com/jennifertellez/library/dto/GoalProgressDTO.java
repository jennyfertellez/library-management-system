package com.jennifertellez.library.dto;

import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingGoal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgressDTO {

    //Goal info
    private Long goalId;
    private Integer targetBooks;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Boolean isActive;

    // Progress metrics
    private Integer booksRead;
    private Integer booksRemaining;
    private Double percentageComplete;

    // Time metrics
    private Long daysElapsed;
    private Long daysRemaining;
    private Long totalDays;

    // Pace metrics
    private Double booksPerMonth;
    private Double booksPerWeek;
    private Double averageBooksPerMonth;
    private Boolean onTrack;

    // Recently finished books in this goal period
    private List<Book> recentlyFinished;

    // Monthly stats
    private Map<String, Integer> monthlyBreakdown = new HashMap<>();

    public static GoalProgressDTO fromGoal(ReadingGoal goal) {
        GoalProgressDTO goalProgressDTO = new GoalProgressDTO();
        goalProgressDTO.setGoalId(goal.getId());
        goalProgressDTO.setTargetBooks(goal.getTargetBooks());
        goalProgressDTO.setYear(goal.getYear());
        goalProgressDTO.setStartDate(goal.getStartDate());
        goalProgressDTO.setEndDate(goal.getEndDate());
        goalProgressDTO.setDescription(goal.getDescription());
        goalProgressDTO.setIsActive(goal.getIsActive());
        goalProgressDTO.setDaysElapsed(goal.getDaysElapsed());
        goalProgressDTO.setDaysRemaining(goal.getDaysRemaining());

        long totalDays = ChronoUnit.DAYS.between(
                goal.getStartDate(),
                goal.getEndDate()
        );
        goalProgressDTO.setTotalDays(totalDays);

        return goalProgressDTO;
    }
}
