package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.ReadingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingGoalRepository extends JpaRepository<ReadingGoal, Long> {

    // Find the current active goal
    Optional<ReadingGoal> findByIsActiveTrue();

    // Find all goals for a specific year
    List<ReadingGoal> findByYearOrderByStartDateDesc(Integer year);

    // Find all goals ordered by most recent
    List<ReadingGoal> findAllByOrderByCreatedAtDesc();

    //Check if a goal exists for a specific date range
    @Query("SELECT COUNT(g) > 0 FROM ReadingGoal g WHERE " +
            "g.id != :excludeId AND " +
            "((g.startDate <= :endDate AND g.endDate >= :startDate))")
    boolean existsOverlappingGoal(Long excludeId, LocalDate startDate, LocalDate endDate);

    // Find current goal (overlaps with today)
    @Query("SELECT g FROM ReadingGoal g WHERE " +
            "g.startDate <= :date AND g.endDate >= :date")
    Optional<ReadingGoal> findCurrentGoal(LocalDate date);
}
