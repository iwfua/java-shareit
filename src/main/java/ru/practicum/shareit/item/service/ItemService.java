package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto findItemById(Long id);

    List<ItemResponseDto> findItemsByOwnerId(Long ownerId);

    ItemResponseDto addItem(CreateItemRequestDto itemDto, Long ownerId);

    UpdateItemResponseDto updateItem(UpdateItemRequestDto itemDto, Long ownerId, Long itemId);

    List<ItemResponseDto> search(String text);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
