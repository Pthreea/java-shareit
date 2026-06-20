package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemRequestDto dto) {
        log.info("POST /requests by user {}", userId);
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests by user {}", userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET /requests/all by user {}", userId);
        return requestService.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        log.info("GET /requests/{} by user {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}