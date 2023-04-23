package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Override
    public long add(Item item) { // returns id of created item
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item.getId();
    }

    @Override
    public Item getByItemId(Long id) {
        return items.get(id);
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Collection<Item> findByText(String text) {
        String toLowerCaseText = text.toLowerCase(Locale.ROOT);
        return items.values().stream()
                .filter(item -> item.isAvailable() &&
                        (item.getName().toLowerCase(Locale.ROOT).contains(toLowerCaseText) ||
                        item.getDescription().toLowerCase(Locale.ROOT).contains(toLowerCaseText)))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Collection<Item> getAll() {
        return items.values();
    }
}
