package ru.practicum.shareit.gateway.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotBlank(message = "Description cannot be empty")
    private String description;
}
