package com.jennifertellez.library.controller;

import com.jennifertellez.library.dto.CreateReadingGoalRequest;
import com.jennifertellez.library.dto.GoalProgressDTO;
import com.jennifertellez.library.dto.ReadingGoalDTO;
import com.jennifertellez.library.dto.UpdateReadingGoalRequest;
import com.jennifertellez.library.model.ReadingGoal;
import com.jennifertellez.library.service.ReadingGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Reading Goals", description = "Endpoints for managing reading goals")
@CrossOrigin(origins = "*")
public class ReadingGoalController {

    private final ReadingGoalService readingGoalService;

    @PostMapping
    @Operation(summary = "Create a new reading goal")
    public ResponseEntity<ReadingGoal> createGoal(@Valid @RequestBody CreateReadingGoalRequest createReadingGoalRequest) {
        ReadingGoal readingGoal = readingGoalService.createGoal(createReadingGoalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(readingGoal);
    }

    @GetMapping
    @Operation(summary = "Get all reading goals")
    public ResponseEntity<List<ReadingGoal>> getAllGoals() {
        List<ReadingGoal> goals = readingGoalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Getting reading goal by ID")
    public ResponseEntity<ReadingGoal> getGoalById(@PathVariable Long id) {
        ReadingGoal goal = readingGoalService.getGoalById(id);
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/active")
    @Operation(summary = "Get currently active goal")
    public ResponseEntity<ReadingGoal> getActiveGoal() {
        ReadingGoal goal = readingGoalService.getActiveGoal();
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/current")
    @Operation(summary = "Get current goal (based on today's date)")
    public ResponseEntity<ReadingGoal> getCurrentGoal() {
        ReadingGoal goal = readingGoalService.getCurrentGoal();
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a reading goal")
    public ResponseEntity<ReadingGoal> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReadingGoalRequest updateReadingGoalRequest) {
        ReadingGoal goal = readingGoalService.updateGoal(id, updateReadingGoalRequest);
        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reading goal")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        readingGoalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "Get progress statistics for a goal")
    public ResponseEntity<GoalProgressDTO> getGoalProgress(@PathVariable Long id) {
        GoalProgressDTO progress = readingGoalService.getGoalProgress(id);
        return ResponseEntity.ok(progress);
    }
}
