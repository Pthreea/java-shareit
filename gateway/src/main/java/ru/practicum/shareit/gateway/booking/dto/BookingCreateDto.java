package ru.practicum.shareit.gateway.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {
    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start must be in present or future")
    private LocalDateTime start;

    @NotNull(message = "End date is required")
    @Future(message = "End must be in future")
    private LocalDateTime end;
}
