package ru.practicum.shareit.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(Long id) {
        super(String.format("Item with id %d not available at the moment.", id));
    }
}