package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findItemById(Long id);

    List<ItemDto> findItemsByOwnerId(Long ownerId);

    ItemDto addItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    List<ItemDto> search(String text);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
