package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserEmailDuplicateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;

@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> usersEmails = new HashMap<>(); // for optimisation, <email, userId>
    private long nextId = 1;

    @Autowired
    private Validator validator;

    @Override
    public User add(User user) {
        validate(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        usersEmails.put(user.getEmail(), user.getId());
        return users.get(user.getId());
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
    public User update(User user) {
        User userToUpdate = get(user.getId());
        User userForUpdate = synthesisOfTwoUsers(userToUpdate, user);
        validate(userForUpdate);
        String oldEmail = users.put(userForUpdate.getId(), userForUpdate).getEmail();
        if (!oldEmail.equals(userForUpdate.getEmail())) {
            usersEmails.remove(oldEmail);
            usersEmails.put(user.getEmail(), user.getId());
        }
        return users.get(user.getId());
    }

    @Override
    public void delete(Long id) {
        User deletedUser = users.remove(id);
        if (deletedUser == null) {
            throw new UserNotFoundException(id);
        }
        usersEmails.remove(deletedUser.getEmail());
    }

    public void validate(User user) {
         Set<ConstraintViolation<User>> violations =  validator.validate(user);
         if (!violations.isEmpty()) {
             throw new ConstraintViolationException(violations);
         }
        // email must be unique
        emailIsUnique(user.getEmail(), user.getId());
    }

    private void emailIsUnique(String email, Long id) {
        if (usersEmails.containsKey(email) && !Objects.equals(usersEmails.get(email), id)) {
            throw new UserEmailDuplicateException(email);
        }
    }

    private User synthesisOfTwoUsers(User target, User source) {
        return User.builder()
                .id(target.getId())
                .name(source.getName() != null ? source.getName() : target.getName())
                .email(source.getEmail() != null ? source.getEmail() : target.getEmail())
                .build();
    }
}
