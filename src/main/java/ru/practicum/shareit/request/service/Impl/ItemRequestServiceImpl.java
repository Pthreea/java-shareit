package ru.practicum.shareit.request.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * Реализация сервиса управления запросами вещей.
 * Обрабатывает бизнес-логику создания и получения запросов на вещи.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Creating item request for user with id: {}", userId);

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.info("Item request created successfully with id: {}", savedRequest.getId());

        return itemRequestMapper.toItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUser(Long userId) {
        log.info("Getting all requests for user with id: {}", userId);

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestor(requestor).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        log.info("Found {} requests for user {}", requests.size(), userId);
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        log.info("Getting all requests except user with id: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        List<ItemRequestDto> requests = itemRequestRepository.findAll().stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        log.info("Found {} requests in total", requests.size());
        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        log.info("Getting request with id: {} for user: {}", requestId, userId);

        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new RuntimeException("Пользователь не найден");
                });

        ItemRequestDto request = itemRequestRepository.findById(requestId)
                .map(itemRequestMapper::toItemRequestDto)
                .orElseThrow(() -> {
                    log.warn("Request not found with id: {}", requestId);
                    return new RuntimeException("Запрос не найден");
                });

        log.info("Request {} retrieved successfully", requestId);
        return request;
    }
}
