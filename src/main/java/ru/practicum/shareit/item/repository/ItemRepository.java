package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getItems(Integer ownerId);

    Item addItem(Item item);

    Item findById(Integer id);

    Item updateItem(Item item, Integer itemId);

    List<Item> searchItemByText(String text);
}
