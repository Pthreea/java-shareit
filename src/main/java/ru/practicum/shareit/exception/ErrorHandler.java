package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
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
    private static final String LOG_CONFLICT = "Conflict: {}";
    private static final String LOG_FORBIDDEN = "Forbidden: {}";
    private static final String LOG_BAD_REQUEST = "Bad request: {}";
    private static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    private static final String LOG_ILLEGAL_ARGUMENT = "Illegal argument: {}";
    private static final String LOG_INTERNAL_ERROR = "Internal error: {}";

    private static final String DEFAULT_VALIDATION_MESSAGE = "Validation failed";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NotFoundException e) {
        log.error(LOG_NOT_FOUND, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(ConflictException e) {
        log.error(LOG_CONFLICT, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbidden(ForbiddenException e) {
        log.error(LOG_FORBIDDEN, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(BadRequestException e) {
        log.error(LOG_BAD_REQUEST, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : DEFAULT_VALIDATION_MESSAGE;
        log.error(LOG_VALIDATION_ERROR, errorMessage);
        return Map.of(ERROR_KEY, errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException e) {
        log.error(LOG_ILLEGAL_ARGUMENT, e.getMessage());
        return Map.of(ERROR_KEY, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneral(Exception e) {
        log.error(LOG_INTERNAL_ERROR, e.getMessage(), e);
        return Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MESSAGE);
    }
}