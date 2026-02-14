package com.jennifertellez.library.dto;

import jakarta.validation.constraints.*;
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
    @Max(value = 1000, message = "Target cannot exceed 1000 books")
    private Integer targetBooks;

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2100, message = "Year must be before 2100 ")
    private Integer year;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Validation: end date must be after start date
    @AssertTrue(message = "End date must be after start date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}
