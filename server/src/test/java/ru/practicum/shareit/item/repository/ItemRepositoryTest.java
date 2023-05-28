package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(showSql = true)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void populateDB() {
        for (int i = 1; i <= 5; i++) {
            em.persist(User.builder()
                    .name("User" + i)
                    .email("user" + i + "@email.ru")
                    .build());
        }
    }

    @Test
    void findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase() {
        Item item1WithNameInName = Item.builder()
                .name("nAme")
                .description("description")
                .available(true)
                .owner(em.find(User.class, 1L)).build();
        repository.save(item1WithNameInName);

        Item item2WithNameInDescription = Item.builder()
                .name("na me")
                .description("descriptionAnd_naMe")
                .available(true)
                .owner(em.find(User.class, 2L)).build();
        repository.save(item2WithNameInDescription);

        Stream<Item> actual1 = repository.findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(
                "namE",
                Pageable.ofSize(100)
        );

        assertEquals(2, actual1.count());

        Item item3WithoutName = Item.builder()
                .name("na me")
                .description("descriptionMeNa")
                .available(true)
                .owner(em.find(User.class, 3L)).build();
        repository.save(item3WithoutName);

        Stream<Item> actual2 = repository.findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(
                "namE",
                Pageable.ofSize(100)
        );

        assertEquals(2, actual2.count());

        Item item4WithNameButAvailableFalse = Item.builder()
                .name("name")
                .description("descriptionMeNa")
                .available(false)
                .owner(em.find(User.class, 4L)).build();
        repository.save(item4WithNameButAvailableFalse);

        Stream<Item> actual3 = repository.findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(
                "namE",
                Pageable.ofSize(100)
        );

        assertEquals(2, actual3.count());
    }
}