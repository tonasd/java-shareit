
package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler ({ConstraintViolationException.class, ItemNotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected String handleConstraintViolationException(final RuntimeException e) {
        log.warn(e.toString());
        return e.getMessage();
    }

    @ExceptionHandler ({ItemNotFoundException.class, UserNotFoundException.class, BookingNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected String handleNotFoundException(final RuntimeException e) {
        log.warn(e.toString());
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.CONFLICT)
    protected String handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        if (message.contains("EMAIL_UNIQUE")) {
            message = "Email is already registered for another user";
        } else {
            message = "Conflict with server rules";
        }
        log.warn(message);

        return message;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected String handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.warn(e.toString());
        return e.getLocalizedMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, String> handleUnknownStateException(final UnknownStateException e) {
        log.warn(e.toString());
        return Map.of("error", "Unknown state: " + e.getMessage());
    }

}
