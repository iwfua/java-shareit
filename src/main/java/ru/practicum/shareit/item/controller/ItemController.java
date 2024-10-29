package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemResponseDto findItemById(@PathVariable(name = "id") Long itemId) {
        log.info("Пришел GET-запрос /users/{}", itemId);
        ItemResponseDto itemDto = itemService.findItemById(itemId);
        log.info("Отправлен GET-ответ с телом={}", itemDto);
        return itemDto;
    }

    @GetMapping
    public List<ItemResponseDto> getItems(@RequestHeader(OWNER_ID) Long ownerId) {
        log.info("Пришел GET-запрос /users with ownerId={}", ownerId);
        List<ItemResponseDto> itemDtos = itemService.findItemsByOwnerId(ownerId);
        log.info("Отправлен GET-ответ для ownerId = {} с телом={}", ownerId, itemDtos);
        return itemDtos;
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItem(@RequestParam(name = "text") String text) {
        log.info("Пришел GET-запрос /users/search with text={}", text);
        List<ItemResponseDto> itemDtos = itemService.search(text);
        log.info("Отправлен ответ с телом {}", itemDtos);
        return itemDtos;
    }

    @PostMapping
    public ItemResponseDto addItem(@Valid @RequestBody CreateItemRequestDto itemDTO,
                                  @RequestHeader(OWNER_ID) Long ownerId) {
        log.info("Пришел POST-запрос с телом itemDto={} и ownerId={}", itemDTO, ownerId);
        ItemResponseDto itemDto = itemService.addItem(itemDTO, ownerId);
        log.info("Отправлен POST-ответ с телом itemDto={}", itemDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public UpdateItemResponseDto updateItem(@RequestBody UpdateItemRequestDto itemDto,
                                            @RequestHeader(OWNER_ID) Long ownerId,
                                            @PathVariable(name = "itemId") Long itemId) {
        log.info("PATCH-запрос");
        return itemService.updateItem(itemDto, ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(OWNER_ID) Long ownerId,
                                    @PathVariable(name = "itemId") Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Пришел POST-запрос на создание комментария: itemId={}, ownerId={}, commentDto={}", itemId, ownerId, commentDto);

        CommentDto createdComment = itemService.createComment(itemId, ownerId, commentDto);

        log.info("Комментарий создан: commentDto={}", createdComment);
        return createdComment;
    }
}
