package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserRepository {

    long add(User user);

    User get(long id);

    void update(User user);

    void delete(long id);

    Collection<User> getAll();
}
