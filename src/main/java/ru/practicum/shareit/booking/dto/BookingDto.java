package ru.practicum.shareit.booking.dto;

/**
 * TODO Sprint add-bookings.
 */
import lombok.*;
import java.time.LocalDateTime;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long itemId;  // ← Добавьте это поле
    private ItemDto item;
    private UserDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
}
