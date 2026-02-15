package com.jennifertellez.library.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReadingGoalRequest {

    @Min(value = 1, message = "Target must be at least 1 book")
    @Max(value = 1000, message = "target cannot exceed 1000 books")
    private Integer targetBooks;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 500, message = "Description cannot excced 500 characters")
    private String description;

    private Boolean isActive;

    // Validation: if both dates provided, end must be after start
    @AssertTrue(message = "End date must be after the start date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}
