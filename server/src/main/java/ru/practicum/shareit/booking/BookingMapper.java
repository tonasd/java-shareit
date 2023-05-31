package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static Booking mapCreationDtoToBooking(BookingCreationDto bookingCreationDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(bookingCreationDto.getStart());
        booking.setEnd(bookingCreationDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDto mapBookingToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                booking.getStatus().name(),
                UserMapper.mapToUserDto(booking.getBooker()),
                ItemMapper.mapToItemDto(booking.getItem())
        );
    }
}
