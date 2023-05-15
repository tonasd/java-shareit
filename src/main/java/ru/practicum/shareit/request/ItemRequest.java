package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 2024, nullable = false)
    String description;

    @Column(nullable = false)
    Instant created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ToString.Exclude
    User requester;
}
