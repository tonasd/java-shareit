package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createNewUser(@RequestBody UserDto userDto) {
        UserDto dto = userService.create(userDto);
        log.info("Created user {}", dto);
        return dto;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        Collection<UserDto> all = userService.getAll();
        log.info("Given all {} existing users", all.size());
        return all;
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        UserDto dto = userService.get(userId);
        log.info("Given user {}", dto);
        return dto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUserFields(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        UserDto dto = userService.updateUserFields(userDto);
        log.info("Updated user {}", dto);
        return dto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        log.info("Deleted user {}", userId);
    }
}
