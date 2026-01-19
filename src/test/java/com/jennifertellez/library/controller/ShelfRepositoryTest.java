package com.jennifertellez.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jennifertellez.library.dto.CreateShelfRequest;
import com.jennifertellez.library.dto.ShelfResponse;
import com.jennifertellez.library.service.GoogleBooksService;
import com.jennifertellez.library.service.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShelfController.class)
public class ShelfRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ShelfService shelfService;

    @MockitoBean
    private GoogleBooksService googleBooksService;

    private ShelfResponse shelfResponse;
    private CreateShelfRequest createShelfRequest;

    @BeforeEach
    void setup() {
        shelfResponse = new ShelfResponse();
        shelfResponse.setId(1L);
        shelfResponse.setName("Favorites");
        shelfResponse.setDescription("My favorite books");

        createShelfRequest = new CreateShelfRequest();
    }

    //add tests to increase code coverage
}
