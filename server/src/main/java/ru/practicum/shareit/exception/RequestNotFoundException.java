package ru.practicum.shareit.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(long requestId) {
        super(String.format("Item with id %d not found.", requestId));
    }
}
