package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT new ru.practicum.shareit.item.dto.CommentDto(c.id, c.text, c.author.name, c.created) " +
            "FROM Comment AS c " +
            "WHERE c.item.id = :itemId")
    List<CommentDto> findAllByItemId(long itemId);
}
