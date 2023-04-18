package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) {
        User user = userRepository.add(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    public UserDto get(long userId) {
        User user = userRepository.get(userId);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUserFields(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user = userRepository.update(user);
        return UserMapper.mapToUserDto(user);
    }

    public void delete(Long userId) {
        //UB - if delete user and left items - needed to be solved in future
        userRepository.delete(userId);
    }

    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
