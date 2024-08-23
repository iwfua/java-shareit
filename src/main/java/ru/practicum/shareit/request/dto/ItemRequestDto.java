package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemRequestDto {
    private final Integer id;

    @NotBlank
    private String description;

    @NotBlank
    private Integer requestorId;

    @NotBlank
    private LocalDateTime created;
}