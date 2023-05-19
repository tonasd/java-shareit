package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Stream;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Stream<Item> findAllByOwnerId(Long ownerId);

  @Query(value = "SELECT i FROM Item AS i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) like(concat('%', LOWER(:text), '%')) " +
            "OR (LOWER(i.description) like(concat('%', LOWER(:text), '%'))))")
    Stream<Item> findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(String text);

  List<Item> findAllByRequestId(long requestId);
}
