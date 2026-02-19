package com.jennifertellez.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jennifertellez.library.dto.CreateReadingGoalRequest;
import com.jennifertellez.library.dto.GoalProgressDTO;
import com.jennifertellez.library.dto.UpdateReadingGoalRequest;
import com.jennifertellez.library.exception.GlobalExceptionHandler;
import com.jennifertellez.library.exception.ResourceNotFoundException;
import com.jennifertellez.library.model.ReadingGoal;
import com.jennifertellez.library.service.ReadingGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadingGoalController.class)
@ExtendWith(SpringExtension.class)
@DisplayName("ReadingGoal Controller Tests")
@Import(GlobalExceptionHandler.class)
class ReadingGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReadingGoalService readingGoalService;

    private ReadingGoal readingGoal;
    private CreateReadingGoalRequest createReadingGoalRequest;

    @BeforeEach
    void setUp() {
        readingGoal = new ReadingGoal();
        readingGoal.setId(1L);
        readingGoal.setTargetBooks(52);
        readingGoal.setYear(2026);
        readingGoal.setStartDate(LocalDate.of(2026, 1, 1));
        readingGoal.setEndDate(LocalDate.of(2026, 12, 31));
        readingGoal.setDescription("Read 52 books");
        readingGoal.setIsActive(true);

        createReadingGoalRequest = new CreateReadingGoalRequest();
        createReadingGoalRequest.setTargetBooks(52);
        createReadingGoalRequest.setYear(2026);
        createReadingGoalRequest.setStartDate(LocalDate.of(2026, 1, 1));
        createReadingGoalRequest.setEndDate(LocalDate.of(2026, 12, 31));
        createReadingGoalRequest.setDescription("Read 52 books");
    }

    @Test
    void testCreateGoal_withValidRequest_returns201() throws Exception {
        when(readingGoalService.createGoal(any(CreateReadingGoalRequest.class)))
                .thenReturn(readingGoal);

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReadingGoalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.targetBooks").value(52))
                .andExpect(jsonPath("$.year").value(2026));

        verify(readingGoalService).createGoal(any(CreateReadingGoalRequest.class));
    }

    @Test
    void testCreateGoal_withInvalidRequest_returns400() throws Exception {
        CreateReadingGoalRequest invalidRequest = new CreateReadingGoalRequest();
        invalidRequest.setYear(2026);
        invalidRequest.setStartDate(LocalDate.of(2026, 1, 1));
        invalidRequest.setEndDate(LocalDate.of(2026, 12, 31));

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(readingGoalService, never()).createGoal(any());
    }

    @Test
    void testGetAllGoals_returnsGoalList() throws Exception {
        when(readingGoalService.getAllGoals())
                .thenReturn(List.of(readingGoal));

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].targetBooks").value(52));

        verify(readingGoalService).getAllGoals();
    }

    @Test
    void testGetGoalById_whenExists_returnsGoal() throws Exception {
        when(readingGoalService.getGoalById(1L))
                .thenReturn(readingGoal);

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.targetBooks").value(52));

        verify(readingGoalService).getGoalById(1L);
    }

    @Test
    void testGetGoalById_whenNotExists_returns404() throws Exception {
        when(readingGoalService.getGoalById(999L))
                .thenThrow(new ResourceNotFoundException("Goal not found"));

        mockMvc.perform(get("/api/goals/999"))
                .andExpect(status().isNotFound());

        verify(readingGoalService).getGoalById(999L);
    }

    @Test
    void testGetActiveGoal_returnsActiveGoal() throws Exception {
        when(readingGoalService.getActiveGoal())
                .thenReturn(readingGoal);

        mockMvc.perform(get("/api/goals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));

        verify(readingGoalService).getActiveGoal();
    }

    @Test
    void testGetCurrentGoal_returnsCurrentGoal() throws Exception {
        when(readingGoalService.getCurrentGoal())
                .thenReturn(readingGoal);

        mockMvc.perform(get("/api/goals/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(readingGoalService).getCurrentGoal();
    }

    @Test
    void testUpdateGoal_withValidRequest_returns200() throws Exception {
        UpdateReadingGoalRequest updateReadingGoalRequest = new UpdateReadingGoalRequest();
        updateReadingGoalRequest.setTargetBooks(60);

        when(readingGoalService.updateGoal(eq(1L), any(UpdateReadingGoalRequest.class)))
                .thenReturn(readingGoal);

        mockMvc.perform(put("/api/goals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReadingGoalRequest)))
                .andExpect(status().isOk());

        verify(readingGoalService).updateGoal(eq(1L), any(UpdateReadingGoalRequest.class));
    }

    @Test
    void testDeleteGoal_returns204() throws Exception {
        doNothing().when(readingGoalService).deleteGoal(1L);

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isNoContent());

        verify(readingGoalService).deleteGoal(1L);
    }

    @Test
    void testGetGoalProgress_returnsProgressDTO() throws Exception {
        GoalProgressDTO progressDTO = new GoalProgressDTO();
        progressDTO.setGoalId(1L);
        progressDTO.setTargetBooks(52);
        progressDTO.setBooksRead(10);
        progressDTO.setPercentageComplete(19.2);

        when(readingGoalService.getGoalProgress(1L))
                .thenReturn(progressDTO);

        mockMvc.perform(get("/api/goals/1/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalId").value(1))
                .andExpect(jsonPath("$.targetBooks").value(52))
                .andExpect(jsonPath("$.booksRead").value(10));

        verify(readingGoalService).getGoalProgress(1L);
    }



}