package com.jennifertellez.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchCriteria {

    private String searchTerm;
    private ReadingStatus status;
    private String author;
    private Integer minRating;
    private Integer maxRating;
    private Integer minYear;
    private  Integer maxYear;
    private Boolean hasIsbn;
}
