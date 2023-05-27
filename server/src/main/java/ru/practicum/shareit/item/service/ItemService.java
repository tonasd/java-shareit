package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto);

    ItemDto getByItemId(Long itemId);

    ItemWithBookingsAndCommentsDto getByItemId(Long itemId, Long requestFromUserId);

    Collection<ItemWithBookingsDto> getByUserId(Long userId, int from, int size);

    Collection<ItemDto> findByText(String text, int from, int size);


    CommentDto postCommentForItemFromAuthor(String text, Long itemId, Long authorId);
}
