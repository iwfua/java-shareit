package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findItemById(Integer id);

    List<ItemDto> getItems(Integer ownerId);

    ItemDto addItem(ItemDto itemDto, Integer ownerId);

    ItemDto updateItem(ItemDto itemDto, Integer ownerId, Integer itemId);

    List<ItemDto> search(String text);
}
