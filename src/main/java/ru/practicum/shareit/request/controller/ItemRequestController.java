package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

/**
 * REST-контроллер для управления запросами вещей.
 * Обрабатывает HTTP-запросы для создания и получения запросов на вещи.
 */
@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String USER_ID_REQUIRED_MESSAGE = "Требуется заголовок X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    /**
     * Создает новый запрос на вещь.
     *
     * @param itemRequestDto данные запроса
     * @param userId идентификатор пользователя из заголовка
     * @return созданный запрос с HTTP-статусом 201 Created
     * @throws BadRequestException если заголовок X-Sharer-User-Id не передан
     */
    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {

        log.info("POST /requests - Creating request for user: {}", userId);
        validateUserId(userId);

        ItemRequestDto createdRequest = itemRequestService.createRequest(itemRequestDto, userId);
        log.info("Request created with id: {}", createdRequest.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    /**
     * Получает все запросы текущего пользователя.
     * Запросы возвращаются с информацией об ответах (предложенных вещах).
     *
     * @param userId идентификатор пользователя из заголовка
     * @return список запросов пользователя
     * @throws BadRequestException если заголовок X-Sharer-User-Id не передан
     */
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllRequestsByUser(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {

        log.info("GET /requests - Getting all requests for user: {}", userId);
        validateUserId(userId);

        List<ItemRequestDto> requests = itemRequestService.getAllRequestsByUser(userId);
        log.info("Found {} requests for user {}", requests.size(), userId);

        return ResponseEntity.ok(requests);
    }

    /**
     * Получает все запросы других пользователей постранично.
     * Позволяет пользователю просматривать запросы, чтобы предложить свои вещи.
     *
     * @param userId идентификатор пользователя из заголовка
     * @return список всех запросов кроме запросов текущего пользователя
     * @throws BadRequestException если заголовок X-Sharer-User-Id не передан
     */
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {

        log.info("GET /requests/all - Getting all requests except user: {}", userId);
        validateUserId(userId);

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId);
        log.info("Found {} requests", requests.size());

        return ResponseEntity.ok(requests);
    }

    /**
     * Получает конкретный запрос по идентификатору.
     * Доступен любому авторизованному пользователю.
     *
     * @param requestId идентификатор запроса
     * @param userId идентификатор пользователя из заголовка
     * @return данные запроса с предложенными вещами
     * @throws BadRequestException если заголовок X-Sharer-User-Id не передан
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {

        log.info("GET /requests/{} - Getting request for user: {}", requestId, userId);
        validateUserId(userId);

        ItemRequestDto request = itemRequestService.getRequestById(requestId, userId);
        log.info("Request {} retrieved successfully", requestId);

        return ResponseEntity.ok(request);
    }

    /**
     * Валидирует наличие идентификатора пользователя в заголовке.
     * Бросает BadRequestException если userId равен null.
     *
     * @param userId идентификатор пользователя для валидации
     * @throws BadRequestException если userId равен null
     */
    private void validateUserId(Long userId) {
        if (userId == null) {
            log.warn("Request without required header: {}", USER_ID_HEADER);
            throw new BadRequestException(USER_ID_REQUIRED_MESSAGE);
        }
    }
}