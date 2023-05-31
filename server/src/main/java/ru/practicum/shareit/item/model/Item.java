package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id") // такое бы и создалось наименование, написал для уч целей
    @ToString.Exclude // чтобы не было случайных обращений к базе например при выводе в лог
    User owner;


    boolean available; // user intention to share the item

    @Column(nullable = false, length = 64)
    String name;

    @Column(nullable = false, length = 256)
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    ItemRequest request;
}
