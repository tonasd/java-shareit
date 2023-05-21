package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AddItemRequestDtoTest {

    @Autowired
    Validator validator;

    @Test
    void shouldNotAcceptBlankDescription() {
        AddItemRequestDto dto = new AddItemRequestDto();
        //null case
        assertThrows(ConstraintViolationException.class, () -> validate(dto));

        //blank case
        dto.setDescription("  ");
        assertThrows(ConstraintViolationException.class, () -> validate(dto));

        // correct case
        dto.setDescription("correct description");
        assertDoesNotThrow(() -> validate(dto));
    }

    @Test
    void shouldNotAcceptLongerThan2048CharsDescription() {
        AddItemRequestDto dto = new AddItemRequestDto();
        dto.setDescription("a".repeat(2024));
        assertDoesNotThrow(() -> validate(dto));

        dto.setDescription("b".repeat(2048) + " ");
        assertThrows(ConstraintViolationException.class, () -> validate(dto));
    }

    private void validate(AddItemRequestDto o) {
        Set<ConstraintViolation<AddItemRequestDto>> violations = validator.validate(o);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}