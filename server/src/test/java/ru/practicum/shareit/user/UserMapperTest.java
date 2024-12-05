package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateResponseUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toCreateUserDto_FullUserMapping() {
        User user = User.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@example.com")
                .build();

        CreateUserDto result = UserMapper.toCreateUserDto(user);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void fromDto_FullDtoMapping() {
        CreateUserDto userDto = CreateUserDto.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@example.com")
                .build();

        User result = UserMapper.fromDto(userDto);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void toUpdateResponseUserDto_FullUserMapping() {
        User user = User.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@example.com")
                .build();

        UpdateResponseUserDto result = UserMapper.toUpdateResponseUserDto(user);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void toCreateUserDto_NullUser() {
        assertThrows(NullPointerException.class, () -> UserMapper.toCreateUserDto(null));
    }

    @Test
    void fromDto_NullDto() {
        assertThrows(NullPointerException.class, () -> UserMapper.fromDto(null));
    }

    @Test
    void toUpdateResponseUserDto_NullUser() {
        assertThrows(NullPointerException.class, () -> UserMapper.toUpdateResponseUserDto(null));
    }
}