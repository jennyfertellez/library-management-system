package com.jennifertellez.library.controller;

import com.jennifertellez.library.dto.CreateShelfRequest;
import com.jennifertellez.library.dto.ShelfResponse;
import com.jennifertellez.library.dto.UpdateShelfRequest;
import com.jennifertellez.library.service.ShelfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
@Slf4j
public class ShelfController {

    private final ShelfService shelfService;

    @PostMapping
    public ResponseEntity<ShelfResponse> createShelf(@Valid @RequestBody CreateShelfRequest request) {
        log.info("POST /api/shelves - Creating new shelf");
        ShelfResponse response = shelfService.createShelf(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ShelfResponse>> getAllShelves() {
        log.info("GET /api/shelves - Fetching all shelves");
        List<ShelfResponse> shelves = shelfService.getAllShelves();
        return ResponseEntity.ok(shelves);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShelf(@PathVariable Long id) {
        log.info("DELETE /api/shelves/{} - Deleting shelf", id);
        shelfService.deleteShelf(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<ShelfResponse> addBookToShelf (
            @PathVariable Long shelfId,
            @PathVariable Long bookId) {
        log.info("POST /api/shelves/{}/books/{} - Adding book to shelf", shelfId, bookId);
        ShelfResponse response = shelfService.addBookToShelf(shelfId, bookId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<ShelfResponse> removeBookFromShelf (
            @PathVariable Long shelfId,
            @PathVariable Long bookId) {
        log.info("DELETE /api/shelves/{}/books/{} - Removing book from shelf", shelfId, bookId);
        ShelfResponse response = shelfService.removeBookFromShelf(shelfId, bookId);
        return ResponseEntity.ok(response);
    }
}
