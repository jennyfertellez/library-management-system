package com.jennifertellez.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "reading_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer targetBooks;

    @Column(name = "goal_year", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public boolean isCurrent() {
        LocalDate now = LocalDate.now();
        return now.isAfter(startDate.minusDays(1)) && now.isBefore(endDate.plusDays(1));
    }

    public long getDaysRemaining() {
        LocalDate now = LocalDate.now();
        if (now.isAfter(endDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(now, endDate);
    }

    public long getDaysElapsed() {
        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate)) {
            return 0;
        }
        if (now.isAfter(endDate)) {
            return  ChronoUnit.DAYS.between(startDate, endDate);
        }
        return ChronoUnit.DAYS.between(startDate, now);
    }
}