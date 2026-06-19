package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreateDto bookingDto, Long userId) {
        log.info("Creating booking for user {} and item {}", userId, bookingDto.getItemId());

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            log.warn("End date must be after start date");
            throw new BadRequestException("End date must be after start date");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new NotFoundException("User not found with id: " + userId);
                });

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.warn("Item not found with id: {}", bookingDto.getItemId());
                    return new NotFoundException("Item not found with id: " + bookingDto.getItemId());
                });

        if (!item.getAvailable()) {
            log.warn("Item {} is not available for booking", item.getId());
            throw new BadRequestException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(userId)) {
            log.warn("User {} tried to book own item {}", userId, item.getId());
            throw new NotFoundException("Cannot book your own item");
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
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long userId) {
        log.info("User {} {} booking {}", userId, approved ? "approving" : "rejecting", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found with id: {}", bookingId);
                    return new NotFoundException("Booking not found with id: " + bookingId);
                });

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("User {} is not owner of item in booking {}", userId, bookingId);
            throw new ForbiddenException("Only item owner can change booking status");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            log.warn("Booking {} status is already {}", bookingId, booking.getStatus());
            throw new BadRequestException("Booking status is already " + booking.getStatus());
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
                    return new NotFoundException("Booking not found with id: " + bookingId);
                });

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            log.warn("User {} has no access to booking {}", userId, bookingId);
            throw new NotFoundException("You don't have access to this booking");
        }

        log.debug("Booking {} retrieved successfully for user {}", bookingId, userId);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(BookingState state, Long userId) {
        log.info("Getting bookings for booker {} with state: {}", userId, state);

        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("User not found with id: " + userId);
        }

        List<Booking> bookings = getBookingsByBookerAndState(userId, state);

        List<BookingDto> result = bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());

        log.info("Found {} bookings for booker {} with state {}", result.size(), userId, state);
        return result;
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(BookingState state, Long userId) {
        log.info("Getting bookings for owner {} with state: {}", userId, state);

        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("User not found with id: " + userId);
        }

        List<Booking> bookings = getBookingsByOwnerAndState(userId, state);

        List<BookingDto> result = bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());

        log.info("Found {} bookings for owner {} with state {}", result.size(), userId, state);
        return result;
    }

    private List<Booking> getBookingsByBookerAndState(Long bookerId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case CURRENT:
                return bookingRepository.findCurrentByBookerId(bookerId, now);
            case PAST:
                return bookingRepository.findPastByBookerId(bookerId, now);
            case FUTURE:
                return bookingRepository.findFutureByBookerId(bookerId, now);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
            case ALL:
            default:
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
        }
    }

    private List<Booking> getBookingsByOwnerAndState(Long ownerId, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case CURRENT:
                return bookingRepository.findCurrentByOwnerId(ownerId, now);
            case PAST:
                return bookingRepository.findPastByOwnerId(ownerId, now);
            case FUTURE:
                return bookingRepository.findFutureByOwnerId(ownerId, now);
            case WAITING:
                return bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
            case ALL:
            default:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        }
    }
}