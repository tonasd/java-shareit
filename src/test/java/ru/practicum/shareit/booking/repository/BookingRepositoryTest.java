package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private LocalDateTime now = LocalDateTime.now();

    private Booking bookingCurrentOfBooker5AndOwner1;
    private Booking bookingFutureOfBooker5AndOwner2;

    @BeforeAll
    private void populateDBWith() {
        for (int i = 1; i <= 5; i++) {
            userRepository.save(User.builder()
                    .name("User" + i)
                    .email("user" + i + "@email.ru")
                    .build());
        }

        for (long i = 1; i <= 4; i++) {
            itemRepository.save(Item.builder()
                    .name("item" + i)
                    .description("Description of item" + i)
                    .available(true)
                    .owner(userRepository.findById(i).get())
                    .request(null)
                    .build());
        }

        bookingCurrentOfBooker5AndOwner1 = new Booking();
        bookingCurrentOfBooker5AndOwner1.setItem(itemRepository.findById(1L).get());
        bookingCurrentOfBooker5AndOwner1.setBooker(userRepository.findById(5L).get());
        bookingCurrentOfBooker5AndOwner1.setStatus(BookingStatus.APPROVED);
        bookingCurrentOfBooker5AndOwner1.setStart(now.minusSeconds(1));
        bookingCurrentOfBooker5AndOwner1.setEnd(now.plusSeconds(2));
        bookingCurrentOfBooker5AndOwner1 = bookingRepository.save(bookingCurrentOfBooker5AndOwner1);

        bookingFutureOfBooker5AndOwner2 = new Booking();
        bookingFutureOfBooker5AndOwner2.setItem(itemRepository.findById(2L).get());
        bookingFutureOfBooker5AndOwner2.setBooker(userRepository.findById(5L).get());
        bookingFutureOfBooker5AndOwner2.setStatus(BookingStatus.WAITING);
        bookingFutureOfBooker5AndOwner2.setStart(now.plusSeconds(1));
        bookingFutureOfBooker5AndOwner2.setEnd(now.plusSeconds(2));
        bookingFutureOfBooker5AndOwner2 = bookingRepository.save(bookingFutureOfBooker5AndOwner2);

    }

    @Test
    void findBookingByOwnerOrBooker_shouldReturnSame_whenBookerOrOwnerOfSameBooking() {
        long id = bookingFutureOfBooker5AndOwner2.getId();
        long bookerId = bookingFutureOfBooker5AndOwner2.getBooker().getId();
        long ownerId = bookingFutureOfBooker5AndOwner2.getItem().getOwner().getId();

        assertTrue(bookingRepository.findBookingByOwnerOrBooker(id,bookerId).isPresent());
        assertEquals(bookingRepository.findBookingByOwnerOrBooker(id,bookerId).get(),
                bookingRepository.findBookingByOwnerOrBooker(id, ownerId).get());
    }

    @Test
    void findAllCurrentBookerBookings_shouldReturnOne_whenSearchedForBookerWithOneCurrent() {
        long bookerId = bookingCurrentOfBooker5AndOwner1.getBooker().getId();
        Stream<Booking> allBookerBookings = bookingRepository.findAllByBookerId(bookerId, PageRequest.of(0, 1000));
        assertTrue(allBookerBookings.count() > 1);
        Stream<Booking> actual = bookingRepository.findAllCurrentBookerBookings(bookerId, now, PageRequest.of(0, 1000));
        assertEquals(1, actual.count());
    }

    @Test
    void findAllCurrentOwnerBookings_shouldReturnEmptyStream_whenOwnerDoesNotHaveCurrentBookings() {
        long ownerId = bookingFutureOfBooker5AndOwner2.getItem().getOwner().getId();
        assertEquals(0, bookingRepository.findAllCurrentOwnerBookings(ownerId, now, PageRequest.ofSize(100))
                .count());
    }
}