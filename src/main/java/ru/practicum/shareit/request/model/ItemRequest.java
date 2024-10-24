package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequest {
    private Integer id;

    @NotBlank
    private String description;

    @NotBlank
    private Integer requestorId;

    @NotBlank
    private LocalDateTime created;
}
