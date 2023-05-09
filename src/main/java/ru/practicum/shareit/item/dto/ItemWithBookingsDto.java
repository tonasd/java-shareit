package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.repository.BookingIdAndBookerIdOnly;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithBookingsDto extends ItemDto {
    BookingIdAndBookerIdOnly lastBooking;
    BookingIdAndBookerIdOnly nextBooking;
}
