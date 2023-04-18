package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Autowired
    private Validator validator;

    @Override
    public Item add(Item item) {
        validate(item);
        item.setId(nextId++);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getByItemId(Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new ItemNotFoundException(id);
        }
        return item;
    }

    @Override
    public Item update(Item item) {
        Item itemUpd = synthesisOfTwoItems(items.get(item.getId()), item);
        validate(itemUpd);
        items.put(item.getId(), itemUpd);
        return itemUpd;
    }

    @Override
    public Collection<Item> getByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Collection<Item> findByText(String text) {
        String toLowerCaseText = text.toLowerCase();
        List<Item> foundItems = items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(toLowerCaseText) ||
                        item.getDescription().toLowerCase(Locale.ROOT).contains(toLowerCaseText))
                .collect(Collectors.toUnmodifiableList());
        return foundItems;
    }

    private void validate(Item item) {
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Item synthesisOfTwoItems(Item target, Item source) {
        return Item.builder()
                .id(target.getId())
                .owner(target.getOwner())
                .name(source.getName() != null ? source.getName() : target.getName())
                .description(source.getDescription() != null ? source.getDescription() : target.getDescription())
                .available(source.getAvailable() != null ? source.getAvailable() : target.getAvailable())
                .build();
    }
}
