package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreationDto {
    long itemId;

    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    LocalDateTime end;

    long bookerId;

    @AssertTrue(message = "Start must be before end")
    private boolean isStartBeforeEnd() {  //Any method name is ok as long as it begins with `is`
        return Objects.nonNull(start) && Objects.nonNull(end) && start.isBefore(end);
    }
}
