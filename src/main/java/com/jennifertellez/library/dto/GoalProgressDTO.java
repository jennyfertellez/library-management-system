package com.jennifertellez.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgressDTO {
    private Long goalId;
    private Integer targetBooks;
    private Integer booksRead;
    private Integer booksRemaining;
    private Double percentageComplete;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer daysRemaining;
    private Integer totalDays;
    private Double booksPerMonth;
    private Double booksPerWeek;
    private Boolean onTrack;
    private List<BookResponse> recentlyFinishedBooks;
}
