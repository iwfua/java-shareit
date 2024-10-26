package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateRequestUserDto {

    @NotBlank
    String name;

    @Email
    @NotBlank
    String email;
}
