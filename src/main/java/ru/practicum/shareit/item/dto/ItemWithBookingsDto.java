package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.ALWAYS)
    BookingIdAndBookerIdOnly lastBooking;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    BookingIdAndBookerIdOnly nextBooking;

    public ItemWithBookingsDto(ItemWithBookingsDto from) {
        this((ItemDto) from, from.lastBooking, from.nextBooking);
    }

    public ItemWithBookingsDto(ItemDto itemDto, BookingIdAndBookerIdOnly lastBooking, BookingIdAndBookerIdOnly nextBooking) {
        super(itemDto);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
