package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
public class UpdateItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;
}
