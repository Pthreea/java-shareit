package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingCreateDto bookingDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("POST /bookings - Creating booking for user: {}", userId);
        BookingDto created = bookingService.createBooking(bookingDto, userId);
        log.info("Booking created with id: {}", created.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("PATCH /bookings/{} - User {} {} booking",
                bookingId, userId, approved ? "approving" : "rejecting");

        BookingDto updated = bookingService.updateBookingStatus(bookingId, approved, userId);
        log.info("Booking {} status updated to {}", bookingId, updated.getStatus());

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("GET /bookings/{} - Getting booking for user: {}", bookingId, userId);
        BookingDto booking = bookingService.getBookingById(bookingId, userId);

        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookingsByBooker(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("GET /bookings?state={} - Getting bookings for user: {}", state, userId);

        BookingState bookingState = parseState(state);
        List<BookingDto> bookings = bookingService.getAllBookingsByBooker(bookingState, userId);

        log.info("Found {} bookings for user {} with state {}", bookings.size(), userId, state);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        log.info("GET /bookings/owner?state={} - Getting bookings for owner: {}", state, userId);

        BookingState bookingState = parseState(state);
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(bookingState, userId);

        log.info("Found {} bookings for owner {} with state {}", bookings.size(), userId, state);
        return ResponseEntity.ok(bookings);
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown state: {}", state);
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}
