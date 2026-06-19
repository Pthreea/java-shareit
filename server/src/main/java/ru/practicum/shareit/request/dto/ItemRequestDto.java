package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemDto {
        private Long id;
        private String name;
        private Long ownerId;
    }
}
