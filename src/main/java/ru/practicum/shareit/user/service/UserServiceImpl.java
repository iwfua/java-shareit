package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto findUserById(Integer id) {
        return UserMapper.toDto(userRepository.findUserById(id));
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userRepository.addUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        return UserMapper.toDto(userRepository.updateUser(UserMapper.fromDto(userDto), userId));
    }

    @Override
    public UserDto deleteUser(Integer userId) {
        return UserMapper.toDto(userRepository.deleteUser(userId));
    }
}
