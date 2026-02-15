package com.jennifertellez.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jennifertellez.library.dto.CreateReadingGoalRequest;
import com.jennifertellez.library.dto.UpdateReadingGoalRequest;
import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.ReadingGoal;
import com.jennifertellez.library.model.ReadingStatus;
import com.jennifertellez.library.repository.BookRepository;
import com.jennifertellez.library.repository.ReadingGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Reading Goal API Integration Tests")
public class ReadingGoalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReadingGoalRepository readingGoalRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        readingGoalRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void testCreateGoal_withValidData_returns201AndCreatesInDatabase() throws Exception {
        CreateReadingGoalRequest request = new CreateReadingGoalRequest();
        request.setTargetBooks(53);
        request.setYear(2026);
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 12, 31));
        request.setDescription("Read 53 books in 2026");

        MvcResult result = mockMvc.perform(post("/api/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.targetBooks").value(53))
                .andExpect(jsonPath("$.year").value(2026))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.description").value("Read 53 books in 2026"))
                .andReturn();

        List<ReadingGoal> goals = readingGoalRepository.findAll();
        assertThat(goals).hasSize(1);
        assertThat(goals.get(0).getTargetBooks()).isEqualTo(53);
        assertThat(goals.get(0).getIsActive()).isTrue();

    }

    @Test
    void testCreateGoal_withInvalidData_returns400() throws Exception {
        CreateReadingGoalRequest request = new CreateReadingGoalRequest();
        request.setTargetBooks(52);
        request.setYear(2026);
        request.setStartDate(LocalDate.of(2026, 12, 31));
        request.setEndDate(LocalDate.of(2026, 1, 1)); // Invalid

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(readingGoalRepository.findAll()).isEmpty();
    }

    @Test
    void testCreateGoal_whenOverlapExists_returns400() throws Exception {
        ReadingGoal existingGoal = new ReadingGoal();
        existingGoal.setTargetBooks(24);
        existingGoal.setYear(2026);
        existingGoal.setStartDate(LocalDate.of(2026, 1, 1));
        existingGoal.setEndDate(LocalDate.of(2026, 12, 31));
        existingGoal.setIsActive(true);
        readingGoalRepository.save(existingGoal);

        CreateReadingGoalRequest request = new CreateReadingGoalRequest();
        request.setTargetBooks(52);
        request.setYear(2026);
        request.setStartDate(LocalDate.of(2026, 6, 1));
        request.setEndDate(LocalDate.of(2027, 6, 1)); // Overlaps

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));

        assertThat(readingGoalRepository.findAll()).hasSize(1);
    }

    @Test
    void testCreateGoal_deactivatesPreviousActiveGoal() throws Exception {
        ReadingGoal existingGoal = new ReadingGoal();
        existingGoal.setTargetBooks(24);
        existingGoal.setYear(2025);
        existingGoal.setStartDate(LocalDate.of(2025, 1, 1));
        existingGoal.setEndDate(LocalDate.of(2025, 12, 31));
        existingGoal.setIsActive(true);
        ReadingGoal saved = readingGoalRepository.save(existingGoal);

        CreateReadingGoalRequest request = new CreateReadingGoalRequest();
        request.setTargetBooks(52);
        request.setYear(2026);
        request.setStartDate(LocalDate.of(2026, 1, 1));
        request.setEndDate(LocalDate.of(2026, 12, 31));

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ReadingGoal oldGoal = readingGoalRepository.findById(saved.getId()).orElseThrow();
        assertThat(oldGoal.getIsActive()).isFalse();

        ReadingGoal newGoal = readingGoalRepository.findByIsActiveTrue().orElseThrow();
        assertThat(newGoal.getYear()).isEqualTo(2026);
    }

    @Test
    void testGetAllGoals_returnsAllGoalsOrderedByDate() throws Exception {
        ReadingGoal goal1 = createGoal(2025, 24);
        ReadingGoal goal2 = createGoal(2026, 52);
        readingGoalRepository.saveAll(List.of(goal1, goal2));

        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].year").value(2026)) // Most recent first
                .andExpect(jsonPath("$[1].year").value(2025));
    }

    @Test
    void testGetAllGoals_whenNoGoals_returnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetGoalById_whenExists_returnsGoal() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        ReadingGoal saved = readingGoalRepository.save(goal);

        mockMvc.perform(get("/api/goals/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.targetBooks").value(52))
                .andExpect(jsonPath("$.year").value(2026));
    }

    @Test
    void testGetGoalById_whenNotExists_returns404() throws Exception {
        mockMvc.perform(get("/api/goals/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void testGetActiveGoal_whenExists_returnsActiveGoal() throws Exception {
        ReadingGoal inactiveGoal = createGoal(2025, 24);
        inactiveGoal.setIsActive(false);

        ReadingGoal activeGoal = createGoal(2026, 52);
        activeGoal.setIsActive(true);

        readingGoalRepository.saveAll(List.of(inactiveGoal, activeGoal));

        mockMvc.perform(get("/api/goals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.year").value(2026));
    }

    @Test
    void testGetActiveGoal_whenNoActiveGoal_returns404() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        goal.setIsActive(false);
        readingGoalRepository.save(goal);

        mockMvc.perform(get("/api/goals/active"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCurrentGoal_whenTodayInRange_returnsGoal() throws Exception {
        // Arrange - Goal that includes today
        ReadingGoal currentGoal = new ReadingGoal();
        currentGoal.setTargetBooks(52);
        currentGoal.setYear(LocalDate.now().getYear());
        currentGoal.setStartDate(LocalDate.now().minusDays(30));
        currentGoal.setEndDate(LocalDate.now().plusDays(30));
        currentGoal.setIsActive(true);
        readingGoalRepository.save(currentGoal);

        mockMvc.perform(get("/api/goals/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(LocalDate.now().getYear()));
    }

    @Test
    void testGetCurrentGoal_whenNoCurrentGoal_returns404() throws Exception {
        ReadingGoal pastGoal = createGoal(2020, 24);
        readingGoalRepository.save(pastGoal);

        mockMvc.perform(get("/api/goals/current"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateGoal_withValidData_updatesSuccessfully() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        ReadingGoal saved = readingGoalRepository.save(goal);

        UpdateReadingGoalRequest updateRequest = new UpdateReadingGoalRequest();
        updateRequest.setTargetBooks(60);
        updateRequest.setDescription("Updated goal");

        mockMvc.perform(put("/api/goals/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetBooks").value(60));

        ReadingGoal updated = readingGoalRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTargetBooks()).isEqualTo(60);
        assertThat(updated.getDescription()).isEqualTo("Updated goal");
    }

    @Test
    void testUpdateGoal_whenNotFound_returns404() throws Exception {
        UpdateReadingGoalRequest updateRequest = new UpdateReadingGoalRequest();
        updateRequest.setTargetBooks(60);

        mockMvc.perform(put("/api/goals/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateGoal_whenDatesOverlap_returns400() throws Exception {
        ReadingGoal goal1 = createGoal(2026, 52);
        ReadingGoal goal2 = createGoal(2027, 52);
        readingGoalRepository.saveAll(List.of(goal1, goal2));

        UpdateReadingGoalRequest updateRequest = new UpdateReadingGoalRequest();
        updateRequest.setStartDate(LocalDate.of(2026, 6, 1));

        mockMvc.perform(put("/api/goals/{id}", goal2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteGoal_whenExists_deletesSuccessfully() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        ReadingGoal saved = readingGoalRepository.save(goal);

        mockMvc.perform(delete("/api/goals/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(readingGoalRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testDeleteGoal_whenNotFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/goals/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGoalProgress_withNoBooksRead_returnsZeroProgress() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        ReadingGoal saved = readingGoalRepository.save(goal);

        mockMvc.perform(get("/api/goals/{id}/progress", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalId").value(saved.getId()))
                .andExpect(jsonPath("$.targetBooks").value(52))
                .andExpect(jsonPath("$.booksRead").value(0))
                .andExpect(jsonPath("$.booksRemaining").value(52))
                .andExpect(jsonPath("$.percentageCompleted").value(0.0))
                .andExpect(jsonPath("$.onTrack").isBoolean());
    }

    @Test
    void testGetGoalProgress_withBooksRead_calculatesCorrectly() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        ReadingGoal saved = readingGoalRepository.save(goal);

        for (int i = 0; i < 10; i++) {
            Book book = new Book();
            book.setTitle("Book " + i);
            book.setAuthor("Author " + i);
            book.setStatus(ReadingStatus.FINISHED);
            book.setFinishedDate(LocalDate.of(2026, 2, 1).plusDays(i));
            bookRepository.save(book);
        }

        mockMvc.perform(get("/api/goals/{id}/progress", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booksRead").value(10))
                .andExpect(jsonPath("$.booksRemaining").value(42))
                .andExpect(jsonPath("$.percentageCompleted").value(greaterThan(0.0)))
                .andExpect(jsonPath("$.booksPerMonth").exists())
                .andExpect(jsonPath("$.booksPerWeek").exists())
                .andExpect(jsonPath("$.averageBooksPerMonth").exists())
                .andExpect(jsonPath("$.onTrack").isBoolean())
                .andExpect(jsonPath("$.recentlyFinished", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.daysElapsed").exists())
                .andExpect(jsonPath("$.daysRemaining").exists());
    }

    @Test
    void testGetGoalProgress_onlyCountsBooksInDateRange() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        ReadingGoal saved = readingGoalRepository.save(goal);

        Book bookInRange = new Book();
        bookInRange.setTitle("Book In Range");
        bookInRange.setStatus(ReadingStatus.FINISHED);
        bookInRange.setFinishedDate(LocalDate.of(2026, 6, 1));
        bookRepository.save(bookInRange);

        Book bookOutOfRange = new Book();
        bookOutOfRange.setTitle("Book Out of Range");
        bookOutOfRange.setStatus(ReadingStatus.FINISHED);
        bookOutOfRange.setFinishedDate(LocalDate.of(2025, 6, 1)); // Before goal
        bookRepository.save(bookOutOfRange);

        mockMvc.perform(get("/api/goals/{id}/progress", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booksRead").value(1)); // Only counts 1 book
    }

    @Test
    void testGetGoalProgress_limitsRecentlyFinishedToFive() throws Exception {
        ReadingGoal goal = createGoal(2026, 52);
        ReadingGoal saved = readingGoalRepository.save(goal);

        for (int i = 0; i < 10; i++) {
            Book book = new Book();
            book.setTitle("Book " + i);
            book.setStatus(ReadingStatus.FINISHED);
            book.setFinishedDate(LocalDate.of(2026, 2, 1).plusDays(i));
            bookRepository.save(book);
        }

        mockMvc.perform(get("/api/goals/{id}/progress", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booksRead").value(10))
                .andExpect(jsonPath("$.recentlyFinished", hasSize(5))); // Limited to 5
    }

    @Test
    void testGetGoalProgress_whenGoalNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/goals/{id}/progress", 999L))
                .andExpect(status().isNotFound());
    }

    /**
     * Helper method to create a ReadingGoal for testing
     */
    private ReadingGoal createGoal(int year, int targetBooks) {
        ReadingGoal goal = new ReadingGoal();
        goal.setTargetBooks(targetBooks);
        goal.setYear(year);
        goal.setStartDate(LocalDate.of(year, 1, 1));
        goal.setEndDate(LocalDate.of(year, 12, 31));
        goal.setDescription("Read " + targetBooks + " books in " + year);
        goal.setIsActive(true);
        return goal;
    }
}

