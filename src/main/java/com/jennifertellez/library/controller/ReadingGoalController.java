package com.jennifertellez.library.controller;

import com.jennifertellez.library.dto.CreateReadingGoalRequest;
import com.jennifertellez.library.dto.GoalProgressDTO;
import com.jennifertellez.library.dto.ReadingGoalDTO;
import com.jennifertellez.library.dto.UpdateReadingGoalRequest;
import com.jennifertellez.library.service.ReadingGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReadingGoalController {

    private final ReadingGoalService readingGoalService;

    @PostMapping
    public ResponseEntity<ReadingGoalDTO> createGoal(@Valid @RequestBody CreateReadingGoalRequest createReadingGoalRequest) {
        ReadingGoalDTO readingGoalDTO = readingGoalService.createGoal(createReadingGoalRequest);
        return new ResponseEntity<>(readingGoalDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReadingGoalDTO>> getAllGoals() {
        List<ReadingGoalDTO> goals = readingGoalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadingGoalDTO> getGoalById(@PathVariable Long id) {
        ReadingGoalDTO goal = readingGoalService.getGoalById(id);
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/current")
    public ResponseEntity<ReadingGoalDTO> getCurrentGoal() {
        ReadingGoalDTO goal = readingGoalService.getCurrentGoal();
        return ResponseEntity.ok(goal);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ReadingGoalDTO>> getCurrentAndFutureGoals() {
        List<ReadingGoalDTO> goals = readingGoalService.getCurrentAndFutureGoals();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<GoalProgressDTO> getGoalProgress(@PathVariable Long id) {
        GoalProgressDTO progress = readingGoalService.getGoalProgress(id);
        return ResponseEntity.ok(progress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReadingGoalDTO> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReadingGoalRequest updateReadingGoalRequest) {
        ReadingGoalDTO goal = readingGoalService.updateGoal(id, updateReadingGoalRequest);
        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        readingGoalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
