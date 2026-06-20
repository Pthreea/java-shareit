package ru.practicum.shareit.gateway.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateDto;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingCreateDto> {

    @Override
    public boolean isValid(BookingCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        if (dto.getStart() == null || dto.getEnd() == null) {
            return true;
        }

        return dto.getEnd().isAfter(dto.getStart());
    }
}
