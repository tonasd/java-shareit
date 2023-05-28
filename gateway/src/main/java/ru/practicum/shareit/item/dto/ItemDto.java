package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.constraints.NullOrNotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    Long id;

    @NotNull(groups = OnCreate.class)
    Boolean available;

    @NotNull(groups = OnCreate.class)
    @NullOrNotBlank
    @Size(max = 64) String name;

    @NotNull(groups = OnCreate.class)
    @NullOrNotBlank
    @Size(max = 256) String description;

    Long requestId;

    public interface OnCreate {
    }

}
