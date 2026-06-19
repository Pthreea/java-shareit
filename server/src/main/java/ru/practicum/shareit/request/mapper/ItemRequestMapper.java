package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        if (itemRequest == null) return null;
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }

    public ItemRequest toEntity(ItemRequestDto dto) {
        if (dto == null) return null;
        return ItemRequest.builder()
                .description(dto.getDescription())
                .build();
    }

    public ItemRequestDto.ItemDto itemToDto(Item item) {
        if (item == null) return null;
        return ItemRequestDto.ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}