package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_KEY = "error";

    private static final String KEYWORD_REQUIRED_HEADER = "Требуется заголовок";
    private static final String KEYWORD_NOT_FOUND_MALE = "не найден";
    private static final String KEYWORD_NOT_FOUND_FEMALE = "не найдена";
    private static final String KEYWORD_NOT_OWNER = "не являетесь владельцем";
    private static final String KEYWORD_NO_ACCESS = "нет доступа";
    private static final String KEYWORD_ALREADY_EXISTS = "уже существует";
    private static final String KEYWORD_DUPLICATE = "дубликат";

    private static final String MESSAGE_INTERNAL_ERROR = "Внутренняя ошибка сервера";
    private static final String MESSAGE_DATA_PROCESSING_ERROR = "Ошибка обработки данных";

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        if (message != null) {
            if (message.contains(KEYWORD_REQUIRED_HEADER)) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
            }

            if (message.contains(KEYWORD_NOT_FOUND_MALE) || message.contains(KEYWORD_NOT_FOUND_FEMALE)) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, message);
            }

            if (message.contains(KEYWORD_NOT_OWNER) || message.contains(KEYWORD_NO_ACCESS)) {
                return buildErrorResponse(HttpStatus.FORBIDDEN, message);
            }

            if (message.contains(KEYWORD_ALREADY_EXISTS) || message.contains(KEYWORD_DUPLICATE)) {
                return buildErrorResponse(HttpStatus.CONFLICT, message);
            }
        }

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointer(NullPointerException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE_DATA_PROCESSING_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(Map.of(ERROR_KEY, message));
    }
}