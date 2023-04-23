package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserEmailDuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Validator validator;
    private final Map<String, Long> usersEmails; // for optimisation, <email, userId>

    public UserService(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
        usersEmails = userRepository.getAll().stream().collect(Collectors.toMap(User::getEmail, User::getId));
    }


    public UserDto create(UserDto userDto) {
        validate(userDto);
        userDto.setId(userRepository.add(UserMapper.mapToUser(userDto)));
        usersEmails.put(userDto.getEmail(), userDto.getId());
        return userDto;
    }

    public UserDto get(long userId) {
        User user = Optional.ofNullable(userRepository.get(userId)).orElseThrow(() -> new UserNotFoundException(userId));
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUserFields(UserDto userDto) {
        UserDto updated = this.get(userDto.getId());

        String oldEmail = updated.getEmail();
        if (userDto.getName() != null) {
            updated.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updated.setEmail(userDto.getEmail());
        }
        validate(updated);

        userRepository.update(UserMapper.mapToUser(updated));
        if (!oldEmail.equals(updated.getEmail())) {
            usersEmails.remove(oldEmail);
            usersEmails.put(updated.getEmail(), updated.getId());
        }
        return updated;
    }

    public void delete(Long userId) {
        //UB - if delete user and left items - needed to be solved in future
        UserDto userDto = this.get(userId);
        userRepository.delete(userId);
        usersEmails.remove(userDto.getEmail());
    }

    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    private void validate(@Valid UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        // email must be unique
        String email = userDto.getEmail();
        if (usersEmails.containsKey(email) && !Objects.equals(usersEmails.get(email), userDto.getId())) {
            throw new UserEmailDuplicateException(email);
        }
    }
}
