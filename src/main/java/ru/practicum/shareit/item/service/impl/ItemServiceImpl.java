package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
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
    @Transactional
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
    public ItemDto getItemById(Long itemId, Long userId) {
        log.info("Getting item by id: {} for user: {}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found with id: {}", itemId);
                    return new NotFoundException("Item not found with id: " + itemId);
                });

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingRepository.findLastBookingByItemId(
                    itemId, now, BookingStatus.APPROVED).orElse(null);
            nextBooking = bookingRepository.findNextBookingByItemId(
                    itemId, now, BookingStatus.APPROVED).orElse(null);
        }

        return itemMapper.toItemDtoWithBookingsAndComments(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        log.info("Getting items for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("User not found with id: " + userId);
        }

        List<Item> items = itemRepository.findByOwnerId(userId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> allComments = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds);
        Map<Long, List<Comment>> commentsByItemId = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> allBookings = bookingRepository.findByItemIdInAndStatus(
                itemIds, BookingStatus.APPROVED);

        Map<Long, Booking> lastBookings = new HashMap<>();
        Map<Long, Booking> nextBookings = new HashMap<>();

        for (Long itemId : itemIds) {
            lastBookings.put(itemId,
                    bookingRepository.findLastBookingByItemId(itemId, now, BookingStatus.APPROVED)
                            .orElse(null));
            nextBookings.put(itemId,
                    bookingRepository.findNextBookingByItemId(itemId, now, BookingStatus.APPROVED)
                            .orElse(null));
        }

        List<ItemDto> result = items.stream()
                .map(item -> itemMapper.toItemDtoWithBookingsAndComments(
                        item,
                        lastBookings.get(item.getId()),
                        nextBookings.get(item.getId()),
                        commentsByItemId.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        log.info("Found {} items for user {}", result.size(), userId);
        return result;
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

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        log.info("Adding comment to item {} by user {}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found with id: {}", itemId);
                    return new NotFoundException("Item not found with id: " + itemId);
                });

        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new NotFoundException("User not found with id: " + userId);
                });

        LocalDateTime now = LocalDateTime.now();
        boolean hasBooking = bookingRepository.existsPastBooking(
                itemId, userId, now, BookingStatus.APPROVED);

        if (!hasBooking) {
            log.warn("User {} hasn't booked item {} yet", userId, itemId);
            throw new BadRequestException("You can only comment on items you have booked");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment added with id: {}", savedComment.getId());

        return commentMapper.toCommentDto(savedComment);
    }
}
