package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Item add(Item item);

    Item getByItemId(Long id);

    Item update(Item item);

    Collection<Item> getByUserId(Long userId);

    Collection<Item> findByText(String text);
}
