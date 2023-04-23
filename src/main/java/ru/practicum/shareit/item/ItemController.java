package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createNewItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto = itemService.create(userId, itemDto);
        log.info("User {} created item {}", userId, itemDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemFields(@RequestBody ItemDto itemDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(itemId);
        itemDto = itemService.update(userId, itemDto);
        log.info("User {} updated item {}", userId, itemDto);
        return itemDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getByItemId(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto itemDto = itemService.getByItemId(itemId);
        log.info("User {} got item {}", userId, itemDto);
        return itemDto;
    }

    @GetMapping
    public Collection<ItemDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        Collection<ItemDto> collection = itemService.getByUserId(userId);
        log.info("Given {} items belong to user {}", collection.size(), userId);
        return collection;
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByText(@RequestParam() String text) {
        Collection<ItemDto> collection = itemService.findByText(text);
        log.info("It has been found {} items with text \"{}\"", collection.size(), text);
        return collection;
    }

}
