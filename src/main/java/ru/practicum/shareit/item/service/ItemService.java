package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto);

    ItemDto getByItemId(Long itemId);

    ItemWithBookingsDto getByItemId(Long itemId, Long requestFromUserId);

    Collection<ItemWithBookingsDto> getByUserId(Long userId);

    Collection<ItemDto> findByText(String text);


}
