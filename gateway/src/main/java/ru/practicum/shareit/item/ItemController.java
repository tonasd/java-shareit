package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated(ItemDto.OnCreate.class)
    public ResponseEntity<Object> postNewItem(@RequestBody @Valid ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.postItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestBody @Valid ItemDto itemDto,
                                            @PathVariable @Positive Long itemId,
                                            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Update itemId={} by userId={}", itemId, userId);
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByItemId(@PathVariable @Positive Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Get itemId={} by userId={}", itemId, userId);
        return itemClient.getByItemId(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "from cannot be negative") int from,
            @RequestParam(defaultValue = "10") @Positive(message = "size must be positive") int size
    ) {
        log.info("Get items of userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @RequestParam() String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "from cannot be negative") int from,
            @RequestParam(defaultValue = "10") @Positive(message = "size must be positive") int size
    ) {
        log.info("Get items with text \"{}\", userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.getByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postCommentForItem(@RequestHeader("X-Sharer-User-id") @Positive Long authorId,
                                                     @PathVariable @Positive long itemId,
                                                     @RequestBody Map<String, String> requestBody) {
        if (!requestBody.containsKey("text") || requestBody.get("text").isBlank()) {
            ResponseStatusException e = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Body must have not empty text property");
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        log.info("Creating comment {} for itemId={} by userId={}", requestBody.get("text"), itemId, authorId);
        return itemClient.postCommentForItemFromAuthor(authorId, itemId, requestBody);
    }

}
