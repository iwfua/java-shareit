package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UpdateResponseUserDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static CreateUserDto toCreateUserDto(User user) {
        return CreateUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromDto(CreateUserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UpdateResponseUserDto toUpdateResponseUserDto(User user) {
        return UpdateResponseUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
