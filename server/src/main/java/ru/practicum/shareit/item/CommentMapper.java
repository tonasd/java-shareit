package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto mapToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment mapToComment(String text, User author, Item item) {
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        comment.setText(text);
        comment.setAuthor(author);
        comment.setItem(item);
        return comment;
    }
}
