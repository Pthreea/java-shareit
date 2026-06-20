package ru.practicum.shareit.gateway.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.gateway.validation.StartBeforeEnd;

import java.time.LocalDateTime;

@Data
@StartBeforeEnd
public class BookingRequestDto {

    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    @NotNull(message = "Start date cannot be null")
    @Future(message = "Start date must be in the future")
    private LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    private LocalDateTime end;
}
