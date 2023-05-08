package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.id = :bookingId " +
            "AND (b.item.owner.id = :userId OR b.booker.id = :userId)")
    Optional<Booking> findBookingByOwnerOrBooker(long bookingId, long userId);

    Stream<Booking> findAllByBookerId(long bookerId, Sort sort);

    Stream<Booking> findAllByBookerIdAndStatusIs(long bookerId, BookingStatus bookingStatus, Sort sort);

    Stream<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime now, Sort sort);

    Stream<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime now, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentBookerBookings(long bookerId, LocalDateTime now, Sort sort);

    Stream<Booking> findAllByItemOwnerIdAndStatusIs(long ownerId, BookingStatus bookingStatus, Sort sort);

    Stream<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime now, Sort sort);

    Stream<Booking> findAllByItemOwnerId(long ownerId, Sort sort);

    Stream<Booking> findAllByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime now, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentOwnerBookings(long ownerId, LocalDateTime now, Sort sort);
}
