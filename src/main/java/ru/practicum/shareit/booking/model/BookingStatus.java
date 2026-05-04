package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,    // Ожидание подтверждения от владельца
    APPROVED,   // Подтверждено владельцем
    REJECTED,   // Отклонено владельцем
    CANCELED    // Отменено арендатором до подтверждения
}
