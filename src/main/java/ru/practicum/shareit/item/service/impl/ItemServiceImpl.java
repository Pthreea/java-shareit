package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Creating item for user with id: {}", userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new NotFoundException("User not found with id: " + userId);
                });

        Item item = itemMapper.toItem(itemDto);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        log.info("Item created with id: {}", savedItem.getId());

        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Updating item with id: {} by user: {}", itemId, userId);

        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("User not found with id: " + userId);
        }

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found with id: {}", itemId);
                    return new NotFoundException("Item not found with id: " + itemId);
                });

        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("User {} is not owner of item {}", userId, itemId);
            throw new ForbiddenException("User is not owner of this item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Item updated with id: {}", updatedItem.getId());

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Getting item by id: {}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found with id: {}", itemId);
                    return new NotFoundException("Item not found with id: " + itemId);
                });

        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        log.info("Getting items for user: {}", userId);

        // Проверка существования пользователя
        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("User not found with id: " + userId);
        }

        List<ItemDto> items = itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Found {} items for user {}", items.size(), userId);
        return items;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Searching items by text: {}", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<ItemDto> items = itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        log.info("Found {} items", items.size());
        return items;
    }
}
