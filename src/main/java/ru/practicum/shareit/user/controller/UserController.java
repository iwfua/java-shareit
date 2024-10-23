package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
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
    public UserDto getUserById(@PathVariable(name = "id") Long userId) {
        log.info("Пришел GET-запрос /users/{}", userId);
        UserDto userDto = userService.findUserById(userId);
        log.info("Отправлен ответ с телом {}", userDto);
        return userDto;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Пришел POST-запрос /users с телом {}", userDto);
        UserDto userDtoRes =  userService.createUser(userDto);
        log.info("Отправлен POST-ответ с телом {}", userDtoRes);
        return userDtoRes;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto,
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
