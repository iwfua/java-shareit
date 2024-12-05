package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long requestId;

    private BookingResponseDto lastBooking;

    private BookingResponseDto nextBooking;

    private List<CommentDto> comments;
}