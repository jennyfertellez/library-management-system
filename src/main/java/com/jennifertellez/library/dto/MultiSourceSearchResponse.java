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
public class MultiSourceSearchResponse {
    private String query;
    private List<BookSearchResult> results;
    private Integer totalResults;
}
