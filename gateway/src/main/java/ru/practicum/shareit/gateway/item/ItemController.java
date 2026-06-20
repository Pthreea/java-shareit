package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Gateway: Creating item for user {}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Gateway: Updating item {} by user {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Gateway: Getting item {} by user {}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Gateway: Getting items for user {}", userId);
        return itemClient.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Gateway: Searching items with text: {}", text);
        return itemClient.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestHeader(USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Gateway: Adding comment to item {} by user {}", itemId, userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
