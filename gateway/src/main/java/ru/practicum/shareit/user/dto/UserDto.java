package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserDto {
    Long id;
    @NotNull(groups = Creation.class)
    @Email String email;

    @NotNull(groups = Creation.class)
    @Size(max = 128) String name;

    public interface Creation {
    }
}
