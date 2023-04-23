package ru.practicum.shareit.exception;

public class UserEmailDuplicateException extends RuntimeException {
    public UserEmailDuplicateException(String email) {
        super(String.format("User with email \"%s\" is already exists", email));
    }
}
