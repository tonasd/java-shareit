package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemRequestWithItemsDto extends ItemRequestDto {

    private List<ItemDto> items;

    public ItemRequestWithItemsDto(ItemRequestDto itemRequestDto, List<ItemDto> items) {
        super(itemRequestDto);
        this.items = items != null ? items : List.of();
    }
}
