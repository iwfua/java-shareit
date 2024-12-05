package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.comments.mapper.CommentsMapper;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.UpdateItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder().id(item.getId())
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(CommentsMapper.toCommentDtoCollection(item.getComments()))
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item, BookingResponseDto lastBooking, BookingResponseDto nextBooking) {
        return ItemResponseDto.builder().id(item.getId())
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(CommentsMapper.toCommentDtoCollection(item.getComments()))
                .build();
    }

    public static UpdateItemResponseDto toUpdateResponseDto(Item item) {
        return UpdateItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public static Collection<ItemResponseDto> toListItemDto(Collection<Item> items) {
        return items.stream()
                .map(item -> ItemMapper.toItemResponseDto(item,
                        BookingMapper.toBookingResponseDto(item.getBookings() == null || item.getBookings().isEmpty() ? null : item.getBookings().getFirst()),
                        BookingMapper.toBookingResponseDto(item.getBookings() == null || item.getBookings().isEmpty() ? null : item.getBookings().getLast())))
                .collect(Collectors.toList());
    }

    public static Item fromRequestDto(CreateItemRequestDto itemDTO) {
        return Item.builder()
                .id(itemDTO.getId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .isAvailable(itemDTO.getAvailable())
                .owner(itemDTO.getOwner())
                .build();
    }

    public static Item fromDtoWithOwner(CreateItemRequestDto itemDTO, User owner) {
        return Item.builder()
                .id(itemDTO.getId())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .isAvailable(itemDTO.getAvailable())
                .owner(owner)
                .build();
    }


    public static Item fromItemResponseDto(ItemResponseDto itemResponseDto) {
        return Item.builder()
                .id(itemResponseDto.getId())
                .name(itemResponseDto.getName())
                .description(itemResponseDto.getDescription())
                .isAvailable(itemResponseDto.getAvailable())
                .owner(itemResponseDto.getOwner())
                .build();
    }

    public static Item fromItemResponseDtoWithOwnerId(ItemResponseDto itemResponseDto, User owner) {
        return Item.builder()
                .id(itemResponseDto.getId())
                .name(itemResponseDto.getName())
                .description(itemResponseDto.getDescription())
                .isAvailable(itemResponseDto.getAvailable())
                .owner(owner)
                .build();
    }
}
