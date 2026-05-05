package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Глобальный обработчик исключений для REST API.
 * Перехватывает исключения и возвращает унифицированные HTTP-ответы.
 */
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

    /**
     * Обрабатывает все RuntimeException и определяет HTTP-статус по содержимому сообщения.
     * Используется для backward compatibility с существующим кодом, который бросает RuntimeException с текстовыми сообщениями.
     *
     * @param ex перехваченное исключение
     * @return ResponseEntity с соответствующим HTTP-статусом и сообщением об ошибке
     */
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

    /**
     * Обрабатывает NullPointerException, возникающие при попытке обращения к null-объектам.
     * Возвращает 500 Internal Server Error, так как такие ошибки указывают на проблемы в коде.
     *
     * @param ex перехваченное исключение
     * @return ResponseEntity с HTTP 500 и сообщением об ошибке
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handleNullPointer(NullPointerException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE_DATA_PROCESSING_ERROR);
    }

    /**
     * Обрабатывает IllegalArgumentException, возникающие при передаче некорректных параметров.
     * Возвращает 400 Bad Request с исходным сообщением об ошибке.
     *
     * @param ex перехваченное исключение
     * @return ResponseEntity с HTTP 400 и сообщением об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Вспомогательный метод для построения унифицированного ответа об ошибке.
     * Инкапсулирует логику создания ResponseEntity, избегая дублирования кода.
     *
     * @param status HTTP-статус ответа
     * @param message текст сообщения об ошибке
     * @return ResponseEntity с указанным статусом и сообщением
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(Map.of(ERROR_KEY, message));
    }
}