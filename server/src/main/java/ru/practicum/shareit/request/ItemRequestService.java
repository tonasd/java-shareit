package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(AddItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> findAllRequesterRequests(long requesterId);

    List<ItemRequestWithItemsDto> findAllPageable(long userId, int from, int size);

    ItemRequestWithItemsDto getRequestById(long userId, long requestId);
}
