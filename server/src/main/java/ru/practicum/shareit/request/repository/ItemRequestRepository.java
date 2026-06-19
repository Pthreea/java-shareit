package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Запросы пользователя
    List<ItemRequest> findByRequestorId(Long requestorId, Sort sort);

    // Запросы других пользователей
    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id != :userId")
    List<ItemRequest> findByRequestorIdNot(@Param("userId") Long userId, Sort sort);
}