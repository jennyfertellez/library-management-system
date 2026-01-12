package com.jennifertellez.library.dto;

import com.jennifertellez.library.model.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String description;
    private String publishedDate;
    private Integer pageCount;
    private String thumbnailUrl;
    private ReadingStatus status;
    private LocalDate finishedDate;
    private Integer rating;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
