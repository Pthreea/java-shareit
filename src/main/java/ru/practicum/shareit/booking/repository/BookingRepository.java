package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;


@Component
public class BookingRepository {
    private final AtomicLong idCounter = new AtomicLong(0);
    private final Map<Long, Booking> bookings = new HashMap<>();

    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            long newId = idCounter.incrementAndGet();
            booking.setId(newId);
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public List<Booking> findAllByBooker(User booker) {
        return bookings.values().stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), booker.getId()))
                .collect(Collectors.toList());
    }

    public List<Booking> findAllByOwner(User owner) {
        return bookings.values().stream()
                .filter(booking -> Objects.equals(booking.getItem().getOwner().getId(), owner.getId()))
                .collect(Collectors.toList());
    }
}
