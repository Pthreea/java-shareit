package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import ru.practicum.shareit.user.model.User;


@Component
public class UserRepository {
    private final AtomicLong idCounter = new AtomicLong(0);
    private final Map<Long, User> users = new HashMap<>();

    public User save(User user) {
        if (user.getId() == null) {
            long newId = idCounter.incrementAndGet();
            user.setId(newId);
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void delete(Long id) {
        users.remove(id);
    }
}
