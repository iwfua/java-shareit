package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto findItemById(@PathVariable(name = "id") Integer itemId) {
        log.info("Пришел GET-запрос /users/{}", itemId);
        ItemDto itemDto = itemService.findItemById(itemId);
        log.info("Отправлен GET-ответ с телом={}", itemDto);
        return itemDto;
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(OWNER_ID) Integer ownerId) {
        log.info("Пришел GET-запрос /users with ownerId={}", ownerId);
        List<ItemDto> itemDtos = itemService.getItems(ownerId);
        log.info("Отправлен GET-ответ для ownerId = {} с телом={}", ownerId, itemDtos);
        return itemDtos;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        log.info("Пришел GET-запрос /users/search with text={}", text);
        List<ItemDto> itemDtos = itemService.search(text);
        log.info("Отправлен ответ с телом {}", itemDtos);
        return itemDtos;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDTO,
                           @RequestHeader(OWNER_ID) Integer ownerId) {
        log.info("Пришел POST-запрос с телом itemDto={} и ownerId={}", itemDTO, ownerId);
        ItemDto itemDto = itemService.addItem(itemDTO, ownerId);
        log.info("Отправлен POST-ответ с телом itemDto={}", itemDto);
        return itemDto;
    }

    //PATCH /items/{itemId}
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(OWNER_ID) Integer ownerId,
                              @PathVariable(name = "itemId") Integer itemId) {
        log.info("PATCH-запрос");
        return itemService.updateItem(itemDto, ownerId, itemId);
    }
}
