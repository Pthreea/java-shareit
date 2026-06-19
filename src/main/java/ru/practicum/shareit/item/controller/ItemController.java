package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("POST /items - Creating item for user: {}", userId);
        ItemDto created = itemService.createItem(userId, itemDto);
        log.info("Item created with id: {}", created.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("PATCH /items/{} - Updating item for user: {}", itemId, userId);
        ItemDto updated = itemService.updateItem(userId, itemId, itemDto);
        log.info("Item {} updated", itemId);

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("GET /items/{} - Getting item for user: {}", itemId, userId);
        ItemDto item = itemService.getItemById(itemId, userId);

        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("GET /items - Getting items for user: {}", userId);
        List<ItemDto> items = itemService.getItemsByUserId(userId);
        log.info("Found {} items for user {}", items.size(), userId);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        log.info("GET /items/search?text={}", text);
        List<ItemDto> items = itemService.searchItems(text);
        log.info("Found {} items", items.size());

        return ResponseEntity.ok(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("POST /items/{}/comment - Adding comment by user: {}", itemId, userId);
        CommentDto created = itemService.addComment(itemId, userId, commentDto);
        log.info("Comment created with id: {}", created.getId());

        return ResponseEntity.ok(created);
    }
}
