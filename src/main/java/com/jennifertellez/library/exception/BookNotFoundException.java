package com.jennifertellez.library.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(Long id) {
        super("Book not found with ID: " + id);
    }
}
