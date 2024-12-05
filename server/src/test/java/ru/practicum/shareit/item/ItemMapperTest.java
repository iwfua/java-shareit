package ru.practicum.shareit.item;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMapperTest {

    @Test
    void testToUpdateResponseDto() {

        Item item = Item.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .isAvailable(true)
                .build();


        UpdateItemResponseDto updateDto = ItemMapper.toUpdateResponseDto(item);


        assertEquals(1L, updateDto.getId());
        assertEquals("Updated Item", updateDto.getName());
        assertEquals("Updated Description", updateDto.getDescription());
        assertTrue(updateDto.getAvailable());
    }

    @Test
    void testToListItemDto_WithBookings() {

        User owner = User.builder().id(1L).build();
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .isAvailable(true)
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .isAvailable(false)
                .owner(owner)
                .build();

        List<Item> items = List.of(item1, item2);


        Collection<ItemResponseDto> itemDtos = ItemMapper.toListItemDto(items);


        assertEquals(2, itemDtos.size());

        List<ItemResponseDto> dtoList = new ArrayList<>(itemDtos);
        assertEquals(1L, dtoList.get(0).getId());
        assertEquals("Item 1", dtoList.get(0).getName());
        assertEquals(2L, dtoList.get(1).getId());
        assertEquals("Item 2", dtoList.get(1).getName());
    }

    @Test
    void testFromRequestDto() {

        CreateItemRequestDto itemDto = CreateItemRequestDto.builder()
                .id(1L)
                .name("New Item")
                .description("New Description")
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();


        Item item = ItemMapper.fromRequestDto(itemDto);


        assertEquals(1L, item.getId());
        assertEquals("New Item", item.getName());
        assertEquals("New Description", item.getDescription());
        assertTrue(item.getIsAvailable());
        assertEquals(itemDto.getOwner(), item.getOwner());
    }

    @Test
    void testFromDtoWithOwner() {

        CreateItemRequestDto itemDto = CreateItemRequestDto.builder()
                .id(1L)
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();
        User owner = User.builder().id(2L).name("Owner").build();

        Item item = ItemMapper.fromDtoWithOwner(itemDto, owner);

        assertEquals(1L, item.getId());
        assertEquals("New Item", item.getName());
        assertEquals("New Description", item.getDescription());
        assertTrue(item.getIsAvailable());
        assertEquals(owner, item.getOwner());
    }

    @Test
    void testFromItemResponseDto() {

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Response Item")
                .description("Response Description")
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        Item item = ItemMapper.fromItemResponseDto(itemResponseDto);

        assertEquals(1L, item.getId());
        assertEquals("Response Item", item.getName());
        assertEquals("Response Description", item.getDescription());
        assertTrue(item.getIsAvailable());
        assertEquals(itemResponseDto.getOwner(), item.getOwner());
    }

    @Test
    void testFromItemResponseDtoWithOwnerId() {

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Response Item")
                .description("Response Description")
                .available(true)
                .build();
        User owner = User.builder().id(2L).name("Owner").build();

        Item item = ItemMapper.fromItemResponseDtoWithOwnerId(itemResponseDto, owner);

        assertEquals(1L, item.getId());
        assertEquals("Response Item", item.getName());
        assertEquals("Response Description", item.getDescription());
        assertTrue(item.getIsAvailable());
        assertEquals(owner, item.getOwner());
    }

    @Test
    void testToListItemDto_EmptyList() {
        Collection<ItemResponseDto> itemDtos = ItemMapper.toListItemDto(Collections.emptyList());

        assertTrue(itemDtos.isEmpty());
    }
}