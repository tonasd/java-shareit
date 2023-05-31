package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({UserDto.Creation.class})
    public ResponseEntity<Object> createNewUser(@RequestBody @Valid UserDto userDto) {
        log.info("Creating user with name={} and email={}", userDto.getName(), userDto.getEmail());
        return userClient.createUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable @Positive long userId) {
        log.info("Get user id={}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUserFields(@PathVariable @Positive long userId,
                                                   @RequestBody @Valid UserDto userDto) {
        log.info("Update user id={}", userId);
        return userClient.updateUserFields(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive long userId) {
        log.info("Delete user id={}", userId);
        return userClient.deleteUserById(userId);
    }
}
