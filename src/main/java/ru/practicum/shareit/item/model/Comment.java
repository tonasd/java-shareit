package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comments",
        uniqueConstraints = {@UniqueConstraint(name = "ONE_USER_ONE_COMMENT", columnNames = {"item_id", "author_id"})})
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(length = 2024, nullable = false)
    String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    User author;

    @Column(nullable = false)
    LocalDateTime created = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id != 0 && id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
