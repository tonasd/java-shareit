package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserRepository {

    User add(User user);

    User get(long id);

    User update(User user);

    void delete(Long id);

    Collection<User> getAll();
}
