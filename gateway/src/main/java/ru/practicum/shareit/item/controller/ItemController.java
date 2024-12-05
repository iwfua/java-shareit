package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@PathVariable(name = "id") Long itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(OWNER_ID) Long ownerId) {
        return itemClient.getItemByUserId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text") String text) {
        return itemClient.getByText(text);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemRequestDto itemDTO,
                                          @RequestHeader(OWNER_ID) Long ownerId) {
        return itemClient.createItem(itemDTO, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody UpdateItemDto itemDto,
                                             @RequestHeader(OWNER_ID) Long ownerId,
                                             @PathVariable(name = "itemId") Long itemId) {
        return itemClient.updateItem(itemDto, ownerId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(OWNER_ID) Long ownerId,
                                                @PathVariable(name = "itemId") Long itemId,
                                                @RequestBody CommentDto commentDto) {
        return itemClient.createComment(ownerId, itemId, commentDto);
    }
}
