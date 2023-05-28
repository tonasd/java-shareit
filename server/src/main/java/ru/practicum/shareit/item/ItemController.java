package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Map;

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
    public ItemWithBookingsAndCommentsDto getByItemId(@PathVariable Long itemId,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemWithBookingsAndCommentsDto itemDto = itemService.getByItemId(itemId, userId);
        log.info("User {} got item {}", userId, itemDto);
        return itemDto;
    }

    @GetMapping
    public Collection<ItemWithBookingsDto> getAllUsersItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        Collection<ItemWithBookingsDto> collection = itemService.getByUserId(userId, from, size);
        log.info("Given {} items belong to user {}", collection.size(), userId);
        return collection;
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByText(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        Collection<ItemDto> collection = itemService.findByText(text, from, size);
        log.info("It has been found {} items with text \"{}\"", collection.size(), text);
        return collection;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postCommentForItem(@RequestHeader("X-Sharer-User-id") Long authorId,
                                         @PathVariable Long itemId,
                                         @RequestBody Map<String, String> requestBody) {
        if (!requestBody.containsKey("text") || requestBody.get("text").isBlank()) {
            RuntimeException e = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Body must have not empty text property");
            log.warn(e.getMessage());
            throw e;
        }
        CommentDto commentDto = itemService.postCommentForItemFromAuthor(requestBody.get("text"), itemId, authorId);
        log.info("Author {} added comment for item {}", authorId, itemId);
        return commentDto;
    }

}
