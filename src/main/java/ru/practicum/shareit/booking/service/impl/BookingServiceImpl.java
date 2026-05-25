package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        log.info("Creating booking for user {} and item {}", userId, bookingDto.getItemId());

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.warn("Item not found with id: {}", bookingDto.getItemId());
                    return new RuntimeException("Вещь не найдена");
                });

        if (!item.getAvailable()) {
            log.warn("Item {} is not available for booking", item.getId());
            throw new RuntimeException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            log.warn("User {} tried to book own item {}", userId, item.getId());
            throw new RuntimeException("Нельзя забронировать свою вещь");
        }

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long userId) {
        log.info("User {} {} booking {}", userId, approved ? "approving" : "rejecting", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found with id: {}", bookingId);
                    return new RuntimeException("Бронирование не найдено");
                });

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("User {} is not owner of item in booking {}", userId, bookingId);
            throw new RuntimeException("Только владелец может изменить статус");
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking {} status changed to {}", bookingId, newStatus);
        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.info("Getting booking {} for user {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found with id: {}", bookingId);
                    return new RuntimeException("Бронирование не найдено");
                });

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            log.warn("User {} has no access to booking {}", userId, bookingId);
            throw new RuntimeException("Нет доступа к этому бронированию");
        }

        log.debug("Booking {} retrieved successfully for user {}", bookingId, userId);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(String state, Long userId) {
        log.info("Getting bookings for booker {} with state: {}", userId, state);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        List<Booking> bookings = bookingRepository.findAllByBooker(booker);
        List<BookingDto> result = filterByState(bookings, state).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());

        log.info("Found {} bookings for booker {} with state {}", result.size(), userId, state);
        return result;
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(String state, Long userId) {
        log.info("Getting bookings for owner {} with state: {}", userId, state);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        List<Booking> bookings = bookingRepository.findAllByOwner(owner);
        List<BookingDto> result = filterByState(bookings, state).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());

        log.info("Found {} bookings for owner {} with state {}", result.size(), userId, state);
        return result;
    }

    private List<Booking> filterByState(List<Booking> bookings, String state) {
        log.debug("Filtering {} bookings by state: {}", bookings.size(), state);

        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            case "ALL":
            default:
                return bookings;
        }
    }
}
