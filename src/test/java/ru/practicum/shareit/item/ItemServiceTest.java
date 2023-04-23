package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
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
        ItemDto wrong = itemDtoList.get(3).toBuilder().name(null).build();
        assertThrows(ConstraintViolationException.class,
                () -> itemService.create(2L, itemDtoList.get(3).toBuilder().name(null).build()));
        assertThrows(ConstraintViolationException.class,
                () -> itemService.create(2L, itemDtoList.get(3).toBuilder().available(null).build()));
        assertThrows(ConstraintViolationException.class,
                () -> itemService.create(2L, itemDtoList.get(3).toBuilder().description(null).build()));
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
    void getByUserId() {
        //no items
        assertIterableEquals(List.of(), itemService.getByUserId(4L));

        //one item
        List<ItemDto> expected = new ArrayList<>(List.of(itemService.create(4L, itemDtoList.get(4))));
        assertIterableEquals(expected, itemService.getByUserId(4L));

        //two items
        expected.add(itemService.create(4L, itemDtoList.get(5)));
        assertIterableEquals(expected, itemService.getByUserId(4L));
    }

    @Test
    void findByText() {
        ItemDto itemDto = itemService.create(6L, itemDtoList.get(6));
        //search in description and caseInsensitive
        assertTrue(itemService.findByText("6 deSc").contains(itemDto)
                && itemService.findByText("6 dEsc").size() == 1);
        //search in name and caseInsensitive
        itemDto.setDescription("updated");
        itemService.update(6L, itemDto);
        assertTrue(itemService.findByText("item6").contains(itemDto)
                && itemService.findByText("item6").size() == 1);
        //if not found
        assertTrue(itemService.findByText("not exist text").isEmpty());
        //search of empty string resulting in empty result
        assertTrue(itemService.findByText("").isEmpty());
        //if not available
        itemDto.setAvailable(false);
        itemService.update(6L, itemDto);
        assertTrue(itemService.findByText("Item6").isEmpty());
    }
}