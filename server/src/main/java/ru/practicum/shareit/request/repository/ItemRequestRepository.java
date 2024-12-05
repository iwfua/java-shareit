package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    Collection<ItemRequest> findAllByRequesterId(Long requesterId);

    Optional<ItemRequest> findByIdOrderByCreatedAsc(Long itemRequestId);
}
