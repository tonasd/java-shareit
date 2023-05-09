package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingSearchState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    private static BookingCreationDto creationDto1;
    private static BookingCreationDto creationDto2;

    @BeforeAll
    void before() {
        LongStream.rangeClosed(1, 10)
                .mapToObj((i) -> UserDto.builder()
                        .name(String.format("User%d", i))
                        .email(String.format("email%d@mail.net", i))
                        .build()).forEach(userService::create);

        LongStream.rangeClosed(1, 10)
                .mapToObj((i) -> ItemDto.builder()
                        .id(i)
                        .available(true)
                        .name(String.format("Item%d", i))
                        .description(String.format("Item%d description", i))
                        .build())
                .forEach(itemDto -> itemService.create(itemDto.getId(), itemDto));

        creationDto1 = new BookingCreationDto();
        creationDto1.setBookerId(5L);
        creationDto1.setItemId(1L);
        creationDto1.setStart(LocalDateTime.now().plusMinutes(20));
        creationDto1.setEnd(creationDto1.getStart().plusMinutes(40));

        creationDto2 = new BookingCreationDto();
        creationDto2.setBookerId(6L);
        creationDto2.setItemId(2L);
        creationDto2.setStart(LocalDateTime.now().plusSeconds(1));
        creationDto2.setEnd(creationDto2.getStart().plusSeconds(2));
    }

    @Test
    void create() {
        BookingDto actual = bookingService.create(creationDto1);
        assertEquals(1L, actual.getId());
        assertEquals(5L, actual.getBooker().getId());
        assertEquals(1L, actual.getItem().getId());
        assertEquals(BookingStatus.WAITING.name(), actual.getStatus());

        //user does not exists
        creationDto2.setBookerId(100L);
        assertThrows(UserNotFoundException.class, () -> bookingService.create(creationDto2));
        creationDto2.setBookerId(6L);

        //item does not exists
        creationDto2.setItemId(100L);
        assertThrows(ItemNotFoundException.class, () -> bookingService.create(creationDto2));
        creationDto2.setItemId(2L);

        //start in past
        creationDto2.setStart(LocalDateTime.now().minusNanos(1));
        assertThrows(ConstraintViolationException.class, () -> bookingService.create(creationDto2));

        //end before start
        creationDto2.setStart(creationDto2.getEnd().plusSeconds(30));
        assertThrows(ConstraintViolationException.class, () -> bookingService.create(creationDto2));

        //start is null
        creationDto2.setStart(null);
        assertThrows(ConstraintViolationException.class, () -> bookingService.create(creationDto2));

        //item is not available
        creationDto2.setStart(creationDto2.getEnd().minusSeconds(1));
        ItemDto item = itemService.getByItemId(creationDto2.getItemId());
        item.setAvailable(false);
        itemService.update(2L, item);
        assertFalse(itemService.getByItemId(item.getId()).getAvailable());
        assertThrows(ItemNotAvailableException.class, () -> bookingService.create(creationDto2));

    }

    @Test
    void ownerAcceptation() {
        BookingDto actual = bookingService.create(creationDto1);
        long bookingId = actual.getId();
        long ownerId = 1;

        //booking not exists
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.ownerAcceptation(100, ownerId, true));

        //booking of another owner
        assertThrows(Exception.class, () -> bookingService.ownerAcceptation(bookingId, ownerId + 1, true));

        //good acceptance
        BookingDto actualAccepted = bookingService.ownerAcceptation(bookingId, ownerId, true);
        assertEquals(BookingStatus.APPROVED.name(), actualAccepted.getStatus());

        //wrong try to change the owner decision
        assertThrows(Exception.class, () -> bookingService.ownerAcceptation(bookingId, ownerId, false));
    }

    @Test
    void findBookingByOwnerOrBooker() {
        BookingDto actual = bookingService.create(creationDto1);
        long bookingId = actual.getId();
        long ownerId = 1;
        long bookerId = actual.getBooker().getId();

        //by owner
        BookingDto byOwner = bookingService.findBookingByOwnerOrBooker(bookingId, ownerId);
        assertEquals(actual, byOwner);

        //by booker
        BookingDto byBooker = bookingService.findBookingByOwnerOrBooker(bookingId, bookerId);
        assertEquals(actual, byBooker);

        //booking not exists
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.findBookingByOwnerOrBooker(bookingId + 10, ownerId));

        // not by owner or booker
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.findBookingByOwnerOrBooker(bookingId, 9));
    }

    @Test
    void findAllBookingsOfBooker() throws InterruptedException {
        long bookerId = 3;
        BookingDto actual1 = bookingService.create(
                new BookingCreationDto(5,
                        LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(5),
                        bookerId)
        );
        BookingDto actual2 = bookingService.create(
                new BookingCreationDto(6,
                        LocalDateTime.now().plusSeconds(3),
                        LocalDateTime.now().plusSeconds(4),
                        bookerId)
        );

        bookingService.ownerAcceptation(actual1.getId(), actual1.getItem().getId() /*itemId=ownerId*/, true);


        //get ALL(2)
        assertEquals(2, bookingService.findAllBookingsOfBooker(bookerId, BookingSearchState.ALL).size());

        //get WAITING(1)
        assertEquals(1, bookingService.findAllBookingsOfBooker(bookerId, BookingSearchState.WAITING).size());

        //get REJECTED(0)
        assertEquals(0, bookingService.findAllBookingsOfBooker(bookerId, BookingSearchState.REJECTED).size());

        // get FUTURE(2)
        List<BookingDto> allBookingsOfBooker = bookingService.findAllBookingsOfBooker(bookerId, BookingSearchState.FUTURE);
        assertEquals(2, allBookingsOfBooker.size());
        assertEquals(actual2.getId(), allBookingsOfBooker.get(0).getId()); //check sorting

        //get CURRENT
        assertTrue(bookingService.findAllBookingsOfBooker(bookerId, BookingSearchState.CURRENT).isEmpty());
        Thread.sleep(2000);
        assertFalse(bookingService.findAllBookingsOfBooker(bookerId, BookingSearchState.CURRENT).isEmpty());
    }

    @Test
    void findAllBookingsOfOwner() throws InterruptedException {
        long ownerId = 7;
        long bookerId = 8;
        BookingDto actual = bookingService.create(
                new BookingCreationDto(7,
                        LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(5),
                        bookerId)
        );

        //get ALL(1)
        assertEquals(1, bookingService.findAllBookingsOfOwner(ownerId, BookingSearchState.ALL).size());

        //get WAITING(1)
        assertEquals(1, bookingService.findAllBookingsOfOwner(ownerId, BookingSearchState.WAITING).size());
        bookingService.ownerAcceptation(actual.getId(), ownerId, false);
        assertTrue(bookingService.findAllBookingsOfOwner(ownerId, BookingSearchState.WAITING).isEmpty());
        assertEquals(1, bookingService.findAllBookingsOfOwner(ownerId, BookingSearchState.ALL).size());


        //get REJECTED(1)
        assertEquals(1, bookingService.findAllBookingsOfOwner(ownerId, BookingSearchState.REJECTED).size());

        //get CURRENT
        assertTrue(bookingService.findAllBookingsOfOwner(ownerId, BookingSearchState.CURRENT).isEmpty());
        Thread.sleep(2000);
        assertFalse(bookingService.findAllBookingsOfOwner(ownerId, BookingSearchState.CURRENT).isEmpty());
    }
}