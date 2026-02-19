package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.BookShelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookShelfRepository extends JpaRepository<BookShelf, Long> {

    @Modifying
    @Query("DELETE FROM BookShelf bs WHERE bs.book.id = :bookId")
    void deleteByBookId(@Param("bookId") Long bookId);

}
