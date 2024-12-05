package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateItemRequestDto {
    private Long id;

    private String name;

    private String description;

    private Long requestId;

    private Boolean available;

    private User owner;
}