package com.jennifertellez.library.controller;

import com.jennifertellez.library.dto.CreateShelfRequest;
import com.jennifertellez.library.dto.ShelfResponse;
import com.jennifertellez.library.dto.UpdateShelfRequest;
import com.jennifertellez.library.service.ShelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Shelves", description = "Shelf management APIs")
@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
@Slf4j
public class ShelfController {

    private final ShelfService shelfService;

    @Operation(
            summary = "Create a new shelf",
            description = "Creates a new shelf with provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shelf created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
    })
    @PostMapping
    public ResponseEntity<ShelfResponse> createShelf(@Valid @RequestBody CreateShelfRequest request) {
        log.info("POST /api/shelves - Creating new shelf");
        ShelfResponse response = shelfService.createShelf(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get all shelves",
            description = "Retrieves all shelves in the library"
    )
    @GetMapping
    public ResponseEntity<List<ShelfResponse>> getAllShelves() {
        log.info("GET /api/shelves - Fetching all shelves");
        List<ShelfResponse> shelves = shelfService.getAllShelves();
        return ResponseEntity.ok(shelves);
    }

    @Operation(
            summary = "Get shelf by ID",
            description = "Retrieves a specific shelf by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shelf found"),
            @ApiResponse(responseCode = "404", description = "Shelf not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShelfResponse> getShelfId(@PathVariable Long id) {
        log.info("GET /api/shelves/{} - Fetching shelf by ID", id);
        ShelfResponse response = shelfService.getShelfById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelfResponse> updateShelf(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShelfRequest request) {
        log.info("PUT /api/shelves/{} - Updating shelf", id);
        ShelfResponse response = shelfService.updateShelf(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete Shelf",
            description = "Delete Shelf"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShelf(@PathVariable Long id) {
        log.info("DELETE /api/shelves/{} - Deleting shelf", id);
        shelfService.deleteShelf(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add a book to shelf",
            description = "Adds a book into a shelf"
    )
    @PostMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<ShelfResponse> addBookToShelf(
            @PathVariable Long shelfId,
            @PathVariable Long bookId) {
        log.info("POST /api/shelves/{}/books/{} - Adding book to shelf", shelfId, bookId);
        ShelfResponse response = shelfService.addBookToShelf(shelfId, bookId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete book from shelf",
            description = "Delete book from shelf"
    )
    @DeleteMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<ShelfResponse> removeBookFromShelf(
            @PathVariable Long shelfId,
            @PathVariable Long bookId) {
        log.info("DELETE /api/shelves/{}/books/{} - Removing book from shelf", shelfId, bookId);
        ShelfResponse response = shelfService.removeBookFromShelf(shelfId, bookId);
        return ResponseEntity.ok(response);
    }
}
