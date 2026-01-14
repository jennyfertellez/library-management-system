package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    Optional<Shelf> findByName(String name);

    boolean existsByName(String name);
}
