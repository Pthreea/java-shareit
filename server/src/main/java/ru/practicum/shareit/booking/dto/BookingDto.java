package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in present or future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    @NotNull(message = "Item id is required")
    private Long itemId;

    private Long bookerId;

    private BookingStatus status;

    private ItemDto item;
    private UserDto booker;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String name;
    }
}
