package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(HEADER) Long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Пришел POST-запрос на создание запроса: userId={}, itemRequestDto={}", userId, itemRequestDto);
        ItemRequestDto createdRequest = itemRequestService.addItemRequest(userId, itemRequestDto);
        log.info("Создан новый запрос: {}", createdRequest);
        return createdRequest;
    }

    @GetMapping
    public Collection<ItemRequestDto> getItemRequestsByOwner(@RequestHeader(HEADER) Long userId) {
        log.info("Пришел GET-запрос на получение запросов пользователя: userId={}", userId);
        Collection<ItemRequestDto> userRequests = itemRequestService.getAllItemRequestByOwner(userId);
        log.info("Получены запросы пользователя: количество={}", userRequests.size());
        return userRequests;
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getItemRequestsAllButOwner(@RequestHeader(HEADER) Long userId,
                                                                 @RequestParam(defaultValue = "0") int from,
                                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("Пришел GET-запрос на получение всех запросов: userId={}, from={}, size={}", userId, from, size);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        Collection<ItemRequestDto> allRequests = itemRequestService.getAllItemRequestToOtherUser(pageable);
        log.info("Получены все запросы: количество={}", allRequests.size());
        return allRequests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId) {
        log.info("Пришел GET-запрос на получение запроса по ID: requestId={}", requestId);
        ItemRequestDto request = itemRequestService.getItemRequestById(requestId);
        log.info("Получен запрос: {}", request);
        return request;
    }
}