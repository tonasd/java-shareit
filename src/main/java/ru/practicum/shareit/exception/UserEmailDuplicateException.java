package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User with this email already exists")
public class UserEmailDuplicateException extends RuntimeException {
    public UserEmailDuplicateException(String email) {
        super(String.format("User with email \"%s\" is already exists", email));
    }
}
