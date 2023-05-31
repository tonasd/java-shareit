package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @RequestParam(name = "from", defaultValue = "0")
                                              @PositiveOrZero(message = "from cannot be negative")
                                              int from,
                                              @RequestParam(name = "size", defaultValue = "10")
                                              @Positive(message = "size must be positive")
                                              int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                             @PathVariable @Positive Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> ownerAcceptation(@RequestHeader("X-Sharer-User-Id") @Positive long ownerId,
                                                   @PathVariable @Positive long bookingId,
                                                   @RequestParam @NotNull Boolean approved) {
        log.info("Patch {} for bookingId={} form userId={}", approved ? "approval" : "rejection", bookingId, ownerId);
        return bookingClient.patchOwnerAcceptation(ownerId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id")
                                                     @Positive long ownerId,
                                                     @RequestParam(name = "state", defaultValue = "all")
                                                     String stateParam,
                                                     @RequestParam(name = "from", defaultValue = "0")
                                                     @PositiveOrZero(message = "from cannot be negative")
                                                     int from,
                                                     @RequestParam(name = "size", defaultValue = "10")
                                                     @Positive(message = "size must be positive")
                                                     int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingsOfOwner(ownerId, state, from, size);
    }
}