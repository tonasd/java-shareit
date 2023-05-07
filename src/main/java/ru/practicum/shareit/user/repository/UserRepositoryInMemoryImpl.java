/*
package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public long add(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public User get(long id) {
        Optional<User> userOptional = Optional.ofNullable(users.get(id));
        return userOptional.orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }
}
*/
