package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    long add(Item item);

    Item getByItemId(Long id);

    void update(Item item);

    Collection<Item> findByText(String text);

    Collection<Item> getAll();
}
