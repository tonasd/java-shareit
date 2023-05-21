package ru.practicum.shareit.item;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@TestPropertySource(properties = {"db.name=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    private static List<ItemDto> itemDtoList;

    @BeforeAll
    void before() {
        LongStream.rangeClosed(1, 10)
                .mapToObj((i) -> UserDto.builder()
                        .name(String.format("User%d", i))
                        .email(String.format("email%d@mail.net", i))
                        .build()).forEach(userService::create);

        itemDtoList = LongStream.rangeClosed(0, 10)
                .mapToObj((i) -> ItemDto.builder()
                        .available(true)
                        .name(String.format("Item%d", i))
                        .description(String.format("Item%d description", i))
                        .build()).collect(Collectors.toUnmodifiableList());
    }

    @Test
    void create() {
        ItemDto actual = itemService.create(1L, itemDtoList.get(1));
        ItemDto expected = itemDtoList.get(1).toBuilder()
                .id(1L)
                .build();
        assertEquals(actual, expected);

        //user not exists
        assertThrows(UserNotFoundException.class, () -> itemService.create(999L, itemDtoList.get(2)));

        //empty fields of dto
        assertThrows(ConstraintViolationException.class,
                () -> itemService.create(2L, itemDtoList.get(3).toBuilder().name(null).build()));
    }

    @Test
    void update() {
        ItemDto current = itemService.create(2L, itemDtoList.get(2));
        ItemDto updatedNameItem = current.toBuilder().name("updated").build();
        assertEquals(updatedNameItem, itemService.update(2L, updatedNameItem));

        ItemDto updatedOnlyOneField = ItemDto.builder().id(current.getId()).available(false).build();
        assertEquals(updatedNameItem.toBuilder().available(false).build(),
                itemService.update(2L, updatedOnlyOneField));

        assertThrows(RuntimeException.class, () -> itemService.update(999L, current));

        //update by not owner
        assertThrows(RuntimeException.class, () -> itemService.update(1L, current));
    }

    @Test
    void getByItemId() {
        ItemDto expected = itemService.create(3L, itemDtoList.get(3));
        assertEquals(expected, itemService.getByItemId(expected.getId()));
    }

    @Test
    void getByItemIdWithBookings() throws InterruptedException {
        ItemDto itemDto = itemService.create(1L, itemDtoList.get(1));
        long itemId = itemDto.getId();
        long ownerId = 1L;
        assertEquals(itemId, itemService.getByItemId(itemDto.getId(), ownerId).getId());
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId).getLastBooking());
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId).getNextBooking());

        long bookerId = 3L;
        long nextBookingId = bookingService.create(
                new BookingCreationDto(itemDto.getId(),
                        LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3),
                        bookerId)
        ).getId();
        long lastBookingId = bookingService.create(
                new BookingCreationDto(itemDto.getId(),
                        LocalDateTime.now().plusNanos(100000000),
                        LocalDateTime.now().plusSeconds(1).plusNanos(500000000),
                        bookerId)
        ).getId();
        Thread.sleep(1000);
        ItemWithBookingsAndCommentsDto itemBeforeAcceptOfBookings = itemService.getByItemId(itemDto.getId(), ownerId);
        MatcherAssert.assertThat(itemBeforeAcceptOfBookings.getLastBooking(), Matchers.nullValue());

        bookingService.ownerAcceptation(lastBookingId, ownerId, true);
        assertEquals(nextBookingId, itemService.getByItemId(itemDto.getId(), ownerId).getNextBooking().getId());
        assertEquals(lastBookingId, itemService.getByItemId(itemDto.getId(), ownerId).getLastBooking().getId());

        //by not owner
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId + 1).getLastBooking());
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId + 1).getNextBooking());
        System.out.println(itemService.getByItemId(itemDto.getId(), ownerId));

    }

    @Test
    void getByUserId() {
        int from = 0;
        int size = 10;

        //no items
        assertIterableEquals(List.of(), itemService.getByUserId(4L, from, size));

        //one item
        Item item = ItemMapper.mapToItem(itemService.create(4L, itemDtoList.get(4)),
                UserMapper.mapToUser(userService.get(4L)), null);
        List<ItemWithBookingsDto> expected = new ArrayList<>(List.of(ItemMapper.mapToItemWithBookingsDto(item, null, null)));
        Collection<ItemWithBookingsDto> byUserId = itemService.getByUserId(4L, from, size);
        assertIterableEquals(expected, byUserId);

        //two items
        Item item2 = ItemMapper.mapToItem(itemService.create(4L, itemDtoList.get(5)),
                UserMapper.mapToUser(userService.get(4L)), null);
        expected.add(ItemMapper.mapToItemWithBookingsDto(item2, null, null));
        assertIterableEquals(expected, itemService.getByUserId(4L, from, size));
    }

    @Test
    void findByText() {
        int from = 0;
        int size = 10;
        ItemDto itemDto = itemService.create(6L, itemDtoList.get(6));
        //search in description and caseInsensitive
        assertTrue(itemService.findByText("6 deSc", from, size).contains(itemDto)
                && itemService.findByText("6 dEsc", from, size).size() == 1);
        //search in name and caseInsensitive
        itemDto.setDescription("updated");
        itemService.update(6L, itemDto);
        assertTrue(itemService.findByText("item6", from, size).contains(itemDto)
                && itemService.findByText("item6", from, size).size() == 1);
        //if not found
        assertTrue(itemService.findByText("not exist text", from, size).isEmpty());
        //search of empty string resulting in empty result
        assertTrue(itemService.findByText("", from, size).isEmpty());
        //if not available
        itemDto.setAvailable(false);
        itemService.update(6L, itemDto);
        assertTrue(itemService.findByText("Item6", from, size).isEmpty());
    }

    @Test
    void postCommentForItemFromAuthor() throws InterruptedException {
        ItemDto dto = itemDtoList.get(7);
        long authorId = 7L;
        ItemDto expected = itemService.create(authorId - 1, dto);
        String text = "text";
        long itemId = expected.getId();
        BookingCreationDto bookingDto = new BookingCreationDto();
        bookingDto.setBookerId(authorId);
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusNanos(100000000));
        bookingDto.setEnd(LocalDateTime.now().plusNanos(120000000));
        BookingDto booking = bookingService.create(bookingDto);
        bookingService.ownerAcceptation(booking.getId(), authorId - 1, true);
        Thread.sleep(1000);


        CommentDto actual = itemService.postCommentForItemFromAuthor(text, itemId, authorId);

        assertEquals(text, actual.getText());
        assertEquals(userService.get(authorId).getName(), actual.getAuthorName());
        assertNotNull(actual.getCreated());
        assertNotEquals(0, actual.getId());
    }
}
