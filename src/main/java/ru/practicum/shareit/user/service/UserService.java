package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateRequestUserDto;
import ru.practicum.shareit.user.dto.UpdateResponseUserDto;
import ru.practicum.shareit.user.dto.CreateUserDto;

public interface UserService {
    CreateUserDto findUserById(Long id);

    CreateUserDto createUser(CreateUserDto userDto);

    UpdateResponseUserDto updateUser(UpdateRequestUserDto userDto, Long userId);

    void deleteUser(Long userId);
}
