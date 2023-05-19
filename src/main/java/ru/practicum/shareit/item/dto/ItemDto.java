package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    Long id;
    @NotNull Boolean available;
    @NotBlank @Size(max = 64) String name;
    @NotBlank @Size(max = 256) String description;
    Long requestId;

    public ItemDto(ItemDto from) {
        this.id = from.id;
        this.available = from.available;
        this.name = from.name;
        this.description = from.description;
        this.requestId = from.requestId;
    }
}
