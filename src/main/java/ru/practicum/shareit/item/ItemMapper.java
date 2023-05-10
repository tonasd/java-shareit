package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.repository.BookingIdAndBookerIdOnly;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.isAvailable())
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

    public static Item mapToItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(Optional.ofNullable(itemDto.getId()).orElse(0L))
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .build();
    }

    public static ItemWithBookingsDto mapToItemWithBookingsDto(Item item,
                                                               BookingIdAndBookerIdOnly lastBooking,
                                                               BookingIdAndBookerIdOnly nextBooking) {
        return new ItemWithBookingsDto(mapToItemDto(item), lastBooking, nextBooking);
    }

    public static ItemWithBookingsAndCommentsDto mapToItemWithBookingsAndCommentsDto(Item item,
                                                                                     BookingIdAndBookerIdOnly lastBooking,
                                                                                     BookingIdAndBookerIdOnly nextBooking,
                                                                                     List<CommentDto> comments) {
        return new ItemWithBookingsAndCommentsDto(mapToItemWithBookingsDto(item, lastBooking, nextBooking), comments);
    }
}

