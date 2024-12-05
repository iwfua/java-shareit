package ru.practicum.shareit.user.dto;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class UpdateUserDto {

    private String name;

    @Email
    private String email;
}
