package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-id") @Positive long requesterId,
                                                @RequestBody @Valid AddItemRequestDto itemRequestDto) {

        log.info("Creating request {}, userId={}", itemRequestDto, requesterId);
        return requestClient.postNewRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequesterRequests(@RequestHeader("X-Sharer-User-id") @Positive long requesterId) {
        log.info("Searching requests of userId={}", requesterId);
        return requestClient.getAllRequesterRequests(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllPageable(@RequestHeader("X-Sharer-User-id") @Positive long userId,
                                                 @RequestParam(defaultValue = "0")
                                                 @PositiveOrZero(message = "from cannot be negative") int from,
                                                 @RequestParam(defaultValue = "10")
                                                 @Positive(message = "size must be positive") int size) {
        log.info("Get requests of other users, userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllOthersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-id") @Positive long userId,
                                                 @PathVariable @Positive long requestId) {
        log.info("Get requestId={}, userId={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
