package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    private final CommentMapper commentMapper;

    public ItemMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public Item toItem(ItemDto dto) {
        if (dto == null) {
            return null;
        }

        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public ItemDto toItemDtoWithBookingsAndComments(
            Item item,
            Booking lastBooking,
            Booking nextBooking,
            List<Comment> comments) {

        if (item == null) {
            return null;
        }

        List<CommentDto> commentDtos = comments != null
                ? comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking != null ? toBookingShortDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? toBookingShortDto(nextBooking) : null)
                .comments(commentDtos)
                .build();
    }

    private ItemDto.BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return ItemDto.BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
