package ru.practicum.shareit.exception;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final String ERROR_KEY = "error";

    private static final String LOG_NOT_FOUND = "Not found: {}";
    private static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    private static final String LOG_DATA_INTEGRITY_VIOLATION = "Data integrity violation: {}";
    private static final String LOG_DUPLICATE = "Duplicate: {}";
    private static final String LOG_FORBIDDEN = "Forbidden: {}";
    private static final String LOG_BAD_REQUEST = "Bad request: {}";
    private static final String LOG_INTERNAL_ERROR = "Internal server error: {}";

    private static final String VALIDATION_ERROR_MESSAGE = "Validation error";
    private static final String EMAIL_DUPLICATE_MESSAGE = "User with this email already exists";
    private static final String DUPLICATE_ENTRY_MESSAGE = "Duplicate entry";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    private static final String EMAIL_KEYWORD = "email";

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException e) {
        log.error(LOG_NOT_FOUND, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e) {
        log.error(LOG_VALIDATION_ERROR, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(VALIDATION_ERROR_MESSAGE);
        log.error(LOG_VALIDATION_ERROR, message);
        return Map.of(ERROR_KEY, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error(LOG_DATA_INTEGRITY_VIOLATION, e.getMessage());
        String message = DUPLICATE_ENTRY_MESSAGE;
        if (e.getMessage() != null && e.getMessage().contains(EMAIL_KEYWORD)) {
            message = EMAIL_DUPLICATE_MESSAGE;
        }
        return Map.of(ERROR_KEY, message);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbiddenException(ForbiddenException e) {
        log.error(LOG_FORBIDDEN, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(BadRequestException e) {
        log.error(LOG_BAD_REQUEST, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception e) {
        log.error(LOG_INTERNAL_ERROR, e.getMessage(), e);
        return Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MESSAGE);
    }
}