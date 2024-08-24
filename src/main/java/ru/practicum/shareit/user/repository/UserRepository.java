package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {
    User findUserById(Integer id);

    User addUser(User user);

    User updateUser(User user, Integer id);

    User deleteUser(Integer userId);
}
