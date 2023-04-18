package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = getItemFromDto(userId,itemDto);
        item = itemRepository.add(item);
        return ItemMapper.mapToItemDto(item);
    }


    @Override public ItemDto update(Long userId, ItemDto itemDto) {
        Item item = getItemFromDto(userId, itemDto);
        validateOwner(item);
        item = itemRepository.update(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override public ItemDto getByItemId(Long itemId) {
        Item item = itemRepository.getByItemId(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override public Collection<ItemDto> getByUserId(Long userId) {
        userRepository.get(userId); // check if user exists
        Collection<Item> userItems = itemRepository.getByUserId(userId);
        return userItems.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toUnmodifiableList());
    }

    @Override public Collection<ItemDto> findByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        Collection<Item> items = itemRepository.findByText(text);
        return items.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toUnmodifiableList());
    }

    private void validateOwner(Item item) {
        long expectedOwnerId = itemRepository.getByItemId(item.getId()).getOwner().getId();
        if (expectedOwnerId != item.getOwner().getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner allowed operation");
        }
    }

    private Item getItemFromDto(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwner(userRepository.get(userId));
        return item;
    }

}
