package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingCreateDto bookingDto, Long userId);

    BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsByBooker(BookingState state, Long userId);

    List<BookingDto> getAllBookingsByOwner(BookingState state, Long userId);
}
