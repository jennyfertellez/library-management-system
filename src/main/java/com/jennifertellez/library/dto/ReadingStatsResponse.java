package com.jennifertellez.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingStatsResponse {

    private Long totalBooks;
    private Long booksRead;
    private Long booksToRead;
    private Long currentlyReading;
    private Long booksDidNotFinish;

    private Integer currentYear;
    private Long booksReadThisYear;
    private Long booksAddedThisYear;

    private Double averageRating;
    private Long ratedBooks;
    private Map<Integer, Long> ratingDistribution;

    private Long uniqueAuthors;
    private Map<String, Long> topAuthors;

    private Double booksPerMonth;
    private Double averagePagesPerBook;

    private Map<Integer, Long> booksReadByYear;

}
