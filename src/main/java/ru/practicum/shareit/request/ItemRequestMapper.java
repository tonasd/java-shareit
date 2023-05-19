package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
                                                      List<Item> items) {
        return new ItemRequestWithItemsDto(ItemRequestMapper.mapToItemRequestDto(itemRequest),
                items.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toUnmodifiableList()));
    }
}
