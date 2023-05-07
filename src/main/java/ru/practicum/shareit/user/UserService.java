package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Validator validator;

    public UserService(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }


    public UserDto create(UserDto userDto) {
        validate(userDto);
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto get(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    public UserDto updateUserFields(UserDto userDto) {
        UserDto userDtoForUpdate = get(userDto.getId());

        if (userDto.getName() != null) {
            userDtoForUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userDtoForUpdate.setEmail(userDto.getEmail());
        }
        validate(userDtoForUpdate);

        User user = userRepository.save(UserMapper.mapToUser(userDtoForUpdate));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }

    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    private void validate(@Valid UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        // email must be unique - checks in DB
    }
}
