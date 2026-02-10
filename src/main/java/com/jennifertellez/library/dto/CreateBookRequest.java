package com.jennifertellez.library.dto;

import com.jennifertellez.library.model.ReadingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @Size(max = 255, message = "Author name must be less than 255 characters")
    private String author;

    @Pattern(
            regexp = "^(\\d{10}|\\d{13}|MAL-\\d+)?$",
            message = "ISBN must be 10 or 13 digits, or MAL ID for manga"
    )
    private String isbn;

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    private String publishedDate;

    private Integer pageCount;

    private String thumbnailUrl;

    private ReadingStatus status = ReadingStatus.TO_READ;

    private String notes;

}
