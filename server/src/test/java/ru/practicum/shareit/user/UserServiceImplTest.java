package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateRequestUserDto;
import ru.practicum.shareit.user.dto.UpdateResponseUserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserDto testCreateUserDto;
    private UpdateRequestUserDto testUpdateRequestUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testCreateUserDto = new CreateUserDto();
        testCreateUserDto.setName("John Doe");
        testCreateUserDto.setEmail("john@example.com");

        testUpdateRequestUserDto = new UpdateRequestUserDto();
        testUpdateRequestUserDto.setName("Updated Name");
        testUpdateRequestUserDto.setEmail("updated@example.com");
    }

    @Test
    void findUserById_ExistingUser_ReturnsUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        CreateUserDto result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void findUserById_NonExistingUser_ThrowsNotFoundException() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void createUser_ValidUserDto_ReturnsCreatedUserDto() {

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        CreateUserDto result = userService.createUser(testCreateUserDto);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_PartialUpdate_ReturnsUpdatedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UpdateResponseUserDto result = userService.updateUser(testUpdateRequestUserDto, 1L);

        assertNotNull(result);
        verify(userRepository).save(argThat(user ->
                user.getName().equals(testUpdateRequestUserDto.getName()) &&
                        user.getEmail().equals(testUpdateRequestUserDto.getEmail())
        ));
    }

    @Test
    void deleteUser_ValidUserId_DeletesUser() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}