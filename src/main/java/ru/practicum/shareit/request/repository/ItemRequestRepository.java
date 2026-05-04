package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Component
public class ItemRequestRepository {
    private final AtomicLong idCounter = new AtomicLong(0);
    private final Map<Long, ItemRequest> requests = new HashMap<>();

    public ItemRequest save(ItemRequest itemRequest) {
        if (itemRequest.getId() == null) {
            long newId = idCounter.incrementAndGet();
            itemRequest.setId(newId);
        }
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }

    public List<ItemRequest> findAllByRequestor(User requestor) {
        return requests.values().stream()
                .filter(request -> Objects.equals(request.getRequestor().getId(), requestor.getId()))
                .collect(Collectors.toList());
    }

    public List<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }
}
