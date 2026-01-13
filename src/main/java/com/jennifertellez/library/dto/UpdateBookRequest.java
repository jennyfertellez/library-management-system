package com.jennifertellez.library.dto;

import com.jennifertellez.library.model.ReadingStatus;
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
public class UpdateBookRequest {

    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @Size(max = 255, message = "Author must be less than 255 characters")
    private String author;

    @Size(max = 2000, message = "Desciption must be less than 2000 characters")
    private String description;

    private ReadingStatus status;

    private LocalDate finishedDate;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Size(max = 2000, message = "Notes must be less than 2000 characters")
    private String notes;
}
