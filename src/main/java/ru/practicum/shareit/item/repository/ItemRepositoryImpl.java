package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items;
    private int itemIdAtMoment;

    public ItemRepositoryImpl() {
        this.items = new HashMap<>();
    }

    @Override
    public List<Item> getItems(Integer ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public Item addItem(Item item) {
        Integer itemId = hasNextItemId();

        item.setId(itemId);
        items.put(itemId, item);
        log.info("user with id={} successfully created", itemId);
        return items.get(itemId);
    }

    @Override
    public Item findById(Integer id) {
        Item item = items.get(id);
        if (item != null) {
            log.info("item with id={} successfully found", id);
            return item;
        } else {
            log.info("item with id={} not found", id);
            throw new NotFoundException("item not found");
        }
    }

    @Override
    public Item updateItem(Item newItem, Integer itemId) {
        log.info("Item with id={} successfully updated", itemId);
        newItem.setId(itemId);
        items.put(itemId, newItem);
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItemByText(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().equalsIgnoreCase(text) ||
                        item.getDescription().equalsIgnoreCase(text))
                .toList();
    }

    private int hasNextItemId() {
        return itemIdAtMoment++;
    }
}
