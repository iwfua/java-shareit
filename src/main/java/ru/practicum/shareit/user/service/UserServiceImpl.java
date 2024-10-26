package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateRequestUserDto;
import ru.practicum.shareit.user.dto.UpdateResponseUserDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CreateUserDto findUserById(Long id) {
        return UserMapper.toCreateUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found")));
    }

    @Override
    public CreateUserDto createUser(CreateUserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        return UserMapper.toCreateUserDto(userRepository.save(user));
    }

    @Override
    public UpdateResponseUserDto updateUser(UpdateRequestUserDto newUserDto, Long userId) {

        User user = UserMapper.fromDto(findUserById(userId));

        if (newUserDto.getEmail() != null) {
            user.setEmail(newUserDto.getEmail());
        }

        if (newUserDto.getName() != null) {
            user.setName(newUserDto.getName());
        }

        return UserMapper.toUpdateResponseUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
