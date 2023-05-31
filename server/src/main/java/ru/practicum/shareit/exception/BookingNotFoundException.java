package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super(String.format("Booking with id %d not exists.", id));
    }
}
