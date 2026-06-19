package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public Item toItem(ItemDto dto) {
        if (dto == null) return null;
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public ItemDto toItemDto(Item item) {
        if (item == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (item.getOwner() != null) {
            dto.setOwnerId(item.getOwner().getId());
        }
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }

    public ItemDto toItemDtoWithBookingsAndComments(Item item,
                                                    BookingDto lastBooking,
                                                    BookingDto nextBooking,
                                                    List<CommentDto> comments) {
        ItemDto dto = toItemDto(item);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);
        return dto;
    }

    public List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().map(this::toItemDto).collect(Collectors.toList());
    }
}