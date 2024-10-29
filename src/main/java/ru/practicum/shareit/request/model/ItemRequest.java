package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequest {
    private Integer id;

    private String description;

    private Integer requesterId;

    private LocalDateTime created;
}
