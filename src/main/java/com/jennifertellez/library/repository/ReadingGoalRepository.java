package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.ReadingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingGoalRepository extends JpaRepository<ReadingGoal, Long> {

    List<ReadingGoal> findByYearOrderByStartDateDesc(Integer year);

    @Query("SELECT g FROM ReadingGoal g WHERE :date BETWEEN g.startDate AND g.endDate ORDER BY g.startDate DESC")
    Optional<ReadingGoal> findActiveGoalByDate(@Param("date") LocalDate date);

    @Query("SELECT g FROM ReadingGoal g WHERE g.endDate >= :date ORDER BY g.startDate DESC")
    List<ReadingGoal> findCurrentAndFutureGoals(@Param("date") LocalDate date);
}
