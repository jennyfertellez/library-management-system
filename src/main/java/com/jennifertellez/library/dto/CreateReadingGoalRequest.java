package com.jennifertellez.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReadingGoalRequest {

    @NotNull(message = "Target books is required")
    @Min(value = 1, message = "Target must be at least 1 book")
    private Integer targetBooks;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String description;
}
