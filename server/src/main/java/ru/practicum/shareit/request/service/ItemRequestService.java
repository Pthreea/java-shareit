package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;

    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        log.debug("Creating request by user {}: {}", userId, dto.getDescription());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = mapper.toEntity(dto);
        request.setRequestor(user);

        ItemRequest saved = requestRepository.save(request);
        log.debug("Request created with id {}", saved.getId());

        ItemRequestDto result = mapper.toDto(saved);
        result.setItems(new ArrayList<>());
        return result;
    }

    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.debug("Getting requests for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<ItemRequest> requests = requestRepository.findByRequestorId(userId);

        return enrichWithItems(requests);
    }

    public List<ItemRequestDto> getOtherUsersRequests(Long userId) {
        log.debug("Getting other users' requests for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(userId);

        return enrichWithItems(requests);
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.debug("Getting request {} for user {}", requestId, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        List<Item> items = itemRepository.findByRequestId(requestId);

        return toDtoWithItems(request, items);
    }

    private List<ItemRequestDto> enrichWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> toDtoWithItems(
                        request,
                        itemsByRequest.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    private ItemRequestDto toDtoWithItems(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = mapper.toDto(request);

        List<ItemRequestDto.ItemDto> itemDtos = items.stream()
                .map(mapper::itemToDto)
                .collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }
}