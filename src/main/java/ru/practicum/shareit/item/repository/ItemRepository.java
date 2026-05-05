package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemRepository {
    private final AtomicLong idCounter = new AtomicLong(0);
    private final Map<Long, Item> items = new HashMap<>();

    public Item save(Item item) {
        if (item.getId() == null) {
            long newId = idCounter.incrementAndGet();
            item.setId(newId);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAllByOwner(User owner) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), owner.getId()))
                .collect(Collectors.toList());
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null &&
                        Objects.equals(item.getOwner().getId(), ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item -> {
                    String name = item.getName() != null ? item.getName().toLowerCase() : "";
                    String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return name.contains(lowerCaseText) || description.contains(lowerCaseText);
                })
                .collect(Collectors.toList());
    }
}

