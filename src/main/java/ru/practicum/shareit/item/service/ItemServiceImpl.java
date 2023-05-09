package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.repository.BookingIdAndBookerIdOnly;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final Validator validator;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        validate(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Item item = ItemMapper.mapToItem(itemDto, owner);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, ItemDto itemDto) {
        Item item = getItemById(itemDto.getId());

        if (userId != item.getOwner().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner allowed operation");
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        validate(ItemMapper.mapToItemDto(item));
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto getByItemId(Long itemId) {
        return ItemMapper.mapToItemDto(getItemById(itemId));
    }

    public ItemWithBookingsDto getByItemId(Long itemId, Long requestFromUserId) {
        Item item = getItemById(itemId);
        BookingIdAndBookerIdOnly lastBooking = null;
        BookingIdAndBookerIdOnly nextBooking = null;
        if (item.getOwner().getId() == requestFromUserId) {
            LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByEndAsc(itemId, now);
            nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now);
        }

        return ItemMapper.mapToItemWithBookingsDto(item, lastBooking, nextBooking);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemWithBookingsDto> getByUserId(Long userId) {
        // check if user exists
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Collection<Item> items = itemRepository.findAllByOwnerId(userId).collect(Collectors.toUnmodifiableList());
        Collection<ItemWithBookingsDto> itemWithBookingsDtos = new ArrayList<>(items.size());
        LocalDateTime now = LocalDateTime.now();
        for (Item item : items) {
            long itemId = item.getId();
            BookingIdAndBookerIdOnly lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeOrderByEndAsc(itemId, now);
            BookingIdAndBookerIdOnly nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now);
            itemWithBookingsDtos.add(ItemMapper.mapToItemWithBookingsDto(item, lastBooking, nextBooking));
        }

        return itemWithBookingsDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(text)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    private void validate(@Valid ItemDto itemDto) {
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

}
