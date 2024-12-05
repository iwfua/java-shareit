package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateRequestUserDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateResponseUserDto;
import ru.practicum.shareit.user.service.UserService;


@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public CreateUserDto getUserById(@PathVariable(name = "id") Long userId) {
        log.info("Пришел GET-запрос /users/{}", userId);
        CreateUserDto userDto = userService.findUserById(userId);

        log.info("Отправлен ответ с телом {}", userDto);
        return userDto;
    }

    @PostMapping
    public CreateUserDto createUser(@RequestBody CreateUserDto userDto) {
        log.info("Пришел POST-запрос /users с телом {}", userDto);
        CreateUserDto userDtoRes =  userService.createUser(userDto);
        log.info("Отправлен POST-ответ с телом {}", userDtoRes);
        return userDtoRes;
    }

    @PatchMapping("/{id}")
    public UpdateResponseUserDto updateUser(@RequestBody UpdateRequestUserDto userDto,
                                            @PathVariable(name = "id") Long userId) {
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(name = "id") Long userId) {
        log.info("Пришел DELETE-запрос /users/{}", userId);
        userService.deleteUser(userId);
        log.info("Отправлен DELETE-ответ с телом id={}", userId);
    }
}
