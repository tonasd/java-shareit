package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User does not exist")
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found", id));
    }
}
