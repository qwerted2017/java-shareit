package ru.practicum.shareit.exceptions;

public class DuplicateEmail extends RuntimeException {
    public DuplicateEmail(String message) {
        super(message);
    }
}