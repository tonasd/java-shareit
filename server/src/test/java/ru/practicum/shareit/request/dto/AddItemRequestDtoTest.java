package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
class AddItemRequestDtoTest {

    @Autowired
    private JacksonTester<AddItemRequestDto> json;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

    @SneakyThrows
    @Test
    void shouldDeserializeCorrectly() {
        AddItemRequestDto dto = new AddItemRequestDto();
        dto.setDescription("description");
        dto.setRequesterId(5L);

        JsonContent<AddItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
    }

    private void validate(AddItemRequestDto o) {
        Set<ConstraintViolation<AddItemRequestDto>> violations = validator.validate(o);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}