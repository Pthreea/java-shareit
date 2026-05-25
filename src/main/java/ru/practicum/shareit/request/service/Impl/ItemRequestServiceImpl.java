package ru.practicum.shareit.request.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Creating item request for user with id: {}", userId);

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new NotFoundException("Пользователь не найден с id: " + userId);
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

        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("Пользователь не найден с id: " + userId);
        }

        List<ItemRequestDto> requests = itemRequestRepository
                .findByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        log.info("Found {} requests for user {}", requests.size(), userId);
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        log.info("Getting all requests except user with id: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("Пользователь не найден с id: " + userId);
        }

        List<ItemRequestDto> requests = itemRequestRepository.findAll().stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        log.info("Found {} requests in total", requests.size());
        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        log.info("Getting request with id: {} for user: {}", requestId, userId);

        if (!userRepository.existsById(userId)) {
            log.warn("User not found with id: {}", userId);
            throw new NotFoundException("Пользователь не найден с id: " + userId);
        }

        ItemRequestDto request = itemRequestRepository.findById(requestId)
                .map(itemRequestMapper::toItemRequestDto)
                .orElseThrow(() -> {
                    log.warn("Request not found with id: {}", requestId);
                    return new NotFoundException("Запрос не найден с id: " + requestId);
                });

        log.info("Request {} retrieved successfully", requestId);
        return request;
    }
}
