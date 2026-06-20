package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.gateway.booking.dto.BookingCreateDto;
import ru.practicum.shareit.gateway.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody BookingCreateDto requestDto) {  // @Valid автоматически проверит @StartBeforeEnd

        log.info("Gateway: Creating booking by user {}", userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved) {

        log.info("Gateway: Approving booking {} by user {} with status {}", bookingId, userId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable @Positive Long bookingId) {

        log.info("Gateway: Getting booking {} by user {}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {

        log.info("Gateway: Getting bookings for user {} with state {}", userId, state);
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        return bookingClient.getUserBookings(userId, bookingState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {

        log.info("Gateway: Getting owner bookings for user {} with state {}", userId, state);
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        return bookingClient.getOwnerBookings(userId, bookingState);
    }
}