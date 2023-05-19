package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithBookingsAndCommentsDto extends ItemWithBookingsDto {
    List<CommentDto> comments;

    public ItemWithBookingsAndCommentsDto(ItemWithBookingsDto itemWithBookingsDto, List<CommentDto> comments) {
        super(itemWithBookingsDto);
        this.comments = comments;
    }
}
