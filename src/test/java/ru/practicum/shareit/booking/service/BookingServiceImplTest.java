package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingSearchState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @InjectMocks
    private BookingServiceImpl service;

    private BookingCreationDto dto;

    @BeforeEach
    void setUp() {
        dto = new BookingCreationDto();
        dto.setBookerId(1);
        dto.setItemId(1);
        dto.setStart(LocalDateTime.now().plusSeconds(2));
        dto.setEnd(LocalDateTime.now().plusSeconds(3));

        when(userRepository.existsById(anyLong())).thenReturn(true);

    }

    @Test
    void create_shouldThrowException_whenOwnerTryToBoolOwnItem() {
        long ownerId = 123;
        Item item = Item.builder()
                .id(1)
                .owner(User.builder().id(ownerId).build())
                .available(true)
                .build();
        dto.setBookerId(ownerId);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> service.create(dto));
    }

    @Test
    void findAllBookingsOfBooker_shouldInvokeCorrectRepositoryMethod_whenStateIsPast() {
        service.findAllBookingsOfBooker(1, BookingSearchState.PAST, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void findAllBookingsOfBooker_shouldInvokeCorrectRepositoryMethod_whenStateIsApproved() {
        service.findAllBookingsOfBooker(1, BookingSearchState.APPROVED, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByBookerIdAndStatusIs(anyLong(), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void findAllBookingsOfBooker_shouldThrowUserNotFoundException_whenUserNotExists() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.findAllBookingsOfBooker(1, BookingSearchState.ALL, 0, 1));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void findAllBookingsOfOwner_shouldThrowUserNotFoundException_whenUserNotExists() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.findAllBookingsOfOwner(1, BookingSearchState.ALL, 0, 1));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void findAllBookingsOfOwner_shouldInvokeCorrectRepositoryMethod_whenStateIsPast() {
        service.findAllBookingsOfOwner(1, BookingSearchState.PAST, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void findAllBookingsOfOwner_shouldInvokeCorrectRepositoryMethod_whenStateIsFuture() {
        service.findAllBookingsOfOwner(1, BookingSearchState.FUTURE, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void findAllBookingsOfOwner_shouldInvokeCorrectRepositoryMethod_whenStateIsApproved() {
        service.findAllBookingsOfOwner(1, BookingSearchState.APPROVED, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByItemOwnerIdAndStatusIs(anyLong(), any(BookingStatus.class), any(Pageable.class));
    }

}