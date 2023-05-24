package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.repository.BookingIdAndBookerIdOnly;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
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
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item mapToItem(ItemDto itemDto, User owner, ItemRequest request) {
        return Item.builder()
                .id(Optional.ofNullable(itemDto.getId()).orElse(0L))
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .request(request)
                .build();
    }

    public static ItemWithBookingsDto mapToItemWithBookingsDto(Item item,
                                                               BookingIdAndBookerIdOnly lastBooking,
                                                               BookingIdAndBookerIdOnly nextBooking) {
        ItemWithBookingsDto result = new ItemWithBookingsDto();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setAvailable(item.isAvailable());
        result.setLastBooking(lastBooking);
        result.setNextBooking(nextBooking);
        result.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return result;
    }

    public static ItemWithBookingsAndCommentsDto mapToItemWithBookingsAndCommentsDto(Item item,
                                                                                     BookingIdAndBookerIdOnly lastBooking,
                                                                                     BookingIdAndBookerIdOnly nextBooking,
                                                                                     List<CommentDto> comments) {
        ItemWithBookingsAndCommentsDto result = new ItemWithBookingsAndCommentsDto();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setAvailable(item.isAvailable());
        result.setLastBooking(lastBooking);
        result.setNextBooking(nextBooking);
        result.setComments(comments);
        result.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return result;
    }
}

