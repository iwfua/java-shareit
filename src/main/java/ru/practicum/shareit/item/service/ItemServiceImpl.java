package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;


    public ItemServiceImpl(UserService userService, ItemRepository itemRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto findItemById(Integer id) {
        return ItemMapper.toDto(itemRepository.findById(id));
    }

    @Override
    public List<ItemDto> getItems(Integer ownerId) {
        validateUserExist(ownerId);

        return itemRepository.getItems(ownerId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer ownerId) {
        validateUserExist(ownerId);

        return ItemMapper.toDto(itemRepository.addItem(ItemMapper.fromDto(itemDto, ownerId)));
    }

    @Override
    public ItemDto updateItem(ItemDto newItemDto, Integer ownerId, Integer itemId) {
        validateUserExist(ownerId);

        ItemDto oldItemDto = ItemMapper.toDto(itemRepository.findById(itemId));

        if (oldItemDto == null) {
            throw new NotFoundException("item not found");
        }

        if (newItemDto.getAvailable() == null) {
            newItemDto.setAvailable(oldItemDto.getAvailable());
        }

        if (newItemDto.getName() == null) {
            newItemDto.setName(oldItemDto.getName());
        }

        if (newItemDto.getDescription() == null) {
            newItemDto.setDescription(oldItemDto.getDescription());
        }

        return ItemMapper.toDto(itemRepository.updateItem(ItemMapper.fromDto(newItemDto, ownerId), itemId));
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.searchItemByText(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private void validateUserExist(Integer ownerId) {
        if (userService.findUserById(ownerId) == null) {
            throw new NotFoundException("owner not exists");
        }
    }
}
