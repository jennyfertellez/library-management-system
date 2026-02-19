package com.jennifertellez.library.exception;

public class BookDeleteConflictException extends RuntimeException {
    public BookDeleteConflictException(String message) {
        super(message);
    }

    public BookDeleteConflictException(Long id) {
        super("Book with id " + id + " cannot be deleted because it is still on shelves.");
    }
}
