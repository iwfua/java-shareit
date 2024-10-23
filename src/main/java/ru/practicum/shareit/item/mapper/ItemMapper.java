package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .ownerId(item.getOwnerId())
                .build();
    }

    public static Item fromDto(ItemDto itemDTO) {
        return Item.builder()
                .id(itemDTO.getId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .isAvailable(itemDTO.getAvailable())
                .ownerId(itemDTO.getOwnerId())
                .build();
    }

    public static Item fromDtoWithOwner(ItemDto itemDTO, Long ownerId) {
        return Item.builder()
                .id(itemDTO.getId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .isAvailable(itemDTO.getAvailable())
                .ownerId(ownerId)
                .build();
    }
}
