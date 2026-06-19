package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * Маппер для преобразования между ItemRequest и ItemRequestDto.
 */
@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    /**
     * Преобразует DTO в сущность.
     * Поля requestor и created устанавливаются вручную в сервисе.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestor", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequest toItemRequest(ItemRequestDto dto);

    /**
     * Преобразует сущность в DTO.
     */
    ItemRequestDto toItemRequestDto(ItemRequest entity);
}