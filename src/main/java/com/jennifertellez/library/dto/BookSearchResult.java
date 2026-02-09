package com.jennifertellez.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchResult {
    private String source; // "openLibrary", "google", "jikan"
    private String title;
    private String author;
    private String description;
    private String thumbnailUrl;
    private String publishedDate;
    private String pageCount;
    private String isbn;
    private String sourceId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MultiSourceSearchResponse {
    private String query;
    private List<BookSearchResult> results;
    private String totalResults;
}
