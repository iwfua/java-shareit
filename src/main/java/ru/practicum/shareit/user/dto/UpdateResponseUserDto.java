package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class UpdateResponseUserDto {
    private Long id;

    private String name;

    private String email;
}
