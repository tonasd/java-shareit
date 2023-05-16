package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import java.util.List;

@Data
public class ItemRequestWithItemsDto extends ItemRequestDto {

    private List<ItemForItemRequestDto> items;

    public ItemRequestWithItemsDto(ItemRequestDto itemRequestDto, List<ItemForItemRequestDto> items) {
        super(itemRequestDto);
        this.items = items != null ? items : List.of();
    }
}
