package com.jennifertellez.library.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReadingGoalRequest {

    @Min(value = 1, message = "Target must be at least 1 book")
    private Integer targetBooks;

    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
