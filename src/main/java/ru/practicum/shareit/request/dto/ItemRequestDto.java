package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
}
