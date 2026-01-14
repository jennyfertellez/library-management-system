package com.jennifertellez.library.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelfResponse {

    private Long id;
    private String name;
    private String description;
    private Integer bookCount;
    private List<BookResponse> books;
    private LocalDateTime createdAt;

}
