package com.jennifertellez.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_isbn", columnList = "isbn"),
        @Index(name = "idx_title", columnList = "title")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 13)
    private String isbn;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(name = "author")
    private String author;

    @Column(length = 2000)
    private String description;

    @Column(name = "published_date")
    private String publishedDate;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "thumbnail_url")
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatus status = ReadingStatus.TO_READ;

    @Column(name = "finished_date")
    private LocalDate finishedDate;

    @Column(name = "rating")
    private Integer rating;

    @Column(length = 2000)
    private String notes;

    @ManyToMany(mappedBy = "books")
    private Set<Shelf> shelves = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
