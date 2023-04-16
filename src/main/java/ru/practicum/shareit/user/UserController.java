package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Min;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createNewUser(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable @Min(value = 1, message = "User id must be positive number") Long userId) {
        return userService.get(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUserFields(
            @PathVariable @Min(value = 1, message = "User id must be positive number") Long userId,
            @RequestBody UserDto userDto
    ) {
        userDto.setId(userId);
        return userService.updateUserFields(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Min(value = 1, message = "User id must be positive number") Long userId) {
        userService.delete(userId);
    }
}
