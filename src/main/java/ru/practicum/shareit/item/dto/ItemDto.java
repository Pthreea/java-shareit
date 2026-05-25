package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Item name cannot be empty")
    private String name;

    @NotBlank(message = "Item description cannot be empty")
    private String description;

    @NotNull(message = "Available field is required")
    private Boolean available;

    private Long ownerId;
    private Long requestId;
}
