package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User {
    long id;
    String email;
    String name;

}
