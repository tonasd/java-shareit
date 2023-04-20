package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Validator validator;

    @Override public ItemDto create(Long userId, ItemDto itemDto) {
        validate(itemDto);
        User owner = Optional.ofNullable(userRepository.get(userId)).orElseThrow(() -> new UserNotFoundException(userId));
        Item item = ItemMapper.mapToItem(itemDto, owner);
        itemDto.setId(itemRepository.add(item));
        return itemDto;
    }

    @Override public ItemDto update(Long userId, ItemDto itemDto) {
        Item item = itemRepository.getByItemId(itemDto.getId());
        if (item == null) {
            throw new ItemNotFoundException(itemDto.getId());
        }

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
        itemRepository.update(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override public ItemDto getByItemId(Long itemId) {
        Item item = itemRepository.getByItemId(itemId);
        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }
        return ItemMapper.mapToItemDto(item);
    }

    @Override public Collection<ItemDto> getByUserId(Long userId) {
        // check if user exists
        Optional.ofNullable(userRepository.get(userId)).orElseThrow(() -> new UserNotFoundException(userId));

        return itemRepository.getAll().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override public Collection<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    private void validate(@Valid ItemDto itemDto) {
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
