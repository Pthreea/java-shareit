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

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String USER_ID_REQUIRED_MESSAGE = "Требуется заголовок X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

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

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getAllRequestsByUser(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {

        log.info("GET /requests - Getting all requests for user: {}", userId);
        validateUserId(userId);

        List<ItemRequestDto> requests = itemRequestService.getAllRequestsByUser(userId);
        log.info("Found {} requests for user {}", requests.size(), userId);

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {

        log.info("GET /requests/all - Getting all requests except user: {}", userId);
        validateUserId(userId);

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId);
        log.info("Found {} requests", requests.size());

        return ResponseEntity.ok(requests);
    }

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

    private void validateUserId(Long userId) {
        if (userId == null) {
            log.warn("Request without required header: {}", USER_ID_HEADER);
            throw new BadRequestException(USER_ID_REQUIRED_MESSAGE);
        }
    }
}