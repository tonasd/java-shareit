package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    long id;
    String description;
//    Вариант задать форматирование при сериализации и десериализации
//    @JsonFormat(pattern = "dd-MM-YYYY", shape = JsonFormat.Shape.STRING)
    String created;

    public ItemRequestDto(ItemRequestDto obj) {
        this.id = obj.id;
        this.description = obj.description;
        this.created = obj.created;
    }
}
