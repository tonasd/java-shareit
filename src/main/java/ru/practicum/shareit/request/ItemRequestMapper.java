package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(AddItemRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return ItemRequestDto.of(itemRequest.getId(),
                itemRequest.getDescription(),
                formatter.format(itemRequest.getCreated()));
    }

    public static ItemRequestWithItemsDto mapToItemRequestWithItemsDto(ItemRequest itemRequest,
                                                      List<ItemForItemRequestDto> items) {
        return new ItemRequestWithItemsDto(ItemRequestMapper.mapToItemRequestDto(itemRequest),items);
    }
}
