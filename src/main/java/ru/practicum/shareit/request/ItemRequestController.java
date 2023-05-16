package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-id") long requesterId,
                                 @RequestBody AddItemRequestDto itemRequestDto) {

        itemRequestDto.setRequesterId(requesterId);
        ItemRequestDto created = itemRequestService.create(itemRequestDto);
        log.info("Created request {}", created);
        return created;
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> findAllRequesterRequests(@RequestHeader("X-Sharer-User-id") long requesterId) {
        List<ItemRequestWithItemsDto> result = itemRequestService.findAllRequesterRequests(requesterId);
        log.info("Found {} requests of user {}", result.size(), requesterId);
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllPageable(@RequestHeader("X-Sharer-User-id") long userId,
                                               @RequestParam(defaultValue = "0")
                                               @PositiveOrZero(message = "from cannot be negative") int from,
                                               @RequestParam(defaultValue = "10")
                                               @Positive(message = "size must be positive") int size) {
        List<ItemRequestDto> result = itemRequestService.findAllPageable(userId, from, size);
        log.info("For user {} given {} item requests of other users. Requested from {}, size {} ",
                userId, result.size(), from, size);
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(@RequestHeader("X-Sharer-User-id") long userId,
                                                  @PathVariable long requestId) {
        ItemRequestWithItemsDto result = itemRequestService.getRequestById(userId, requestId);
        log.info("User {} got request {}", userId, requestId);
        return result;
    }
}
