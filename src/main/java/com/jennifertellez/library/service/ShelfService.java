package com.jennifertellez.library.service;

import com.jennifertellez.library.dto.CreateShelfRequest;
import com.jennifertellez.library.dto.ShelfResponse;
import com.jennifertellez.library.dto.UpdateShelfRequest;

import java.util.List;

public interface ShelfService {

    ShelfResponse createShelf(CreateShelfRequest request);

    ShelfResponse getShelfById(Long id);

    List<ShelfResponse> getAllShelves();

    ShelfResponse updateShelf(Long id, UpdateShelfRequest request);

    void deleteShelf(Long id);

    ShelfResponse addBookToShelf(Long shelfId, Long bookId);

    ShelfResponse removeBookFromShelf(Long shelfId, Long bookId);
}
