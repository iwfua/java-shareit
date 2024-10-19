package ru.practicum.shareit.user.repository;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users;
    private final Set<String> emailUniqueSet;
    private int userIdAtMoment;

    public UserRepositoryImpl() {
        this.users = new HashMap<>();
        this.emailUniqueSet = new HashSet<>();
    }

    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("user al-ready exist");
        }

        if (emailUniqueSet.contains(user.getEmail())) {
            throw new ConflictException("user with this email al-ready exist");
        }

        int userId = hasNextUserId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Successfully user with {} created", userId);
        emailUniqueSet.add(user.getEmail());
        return users.get(userId);
    }

    public User findUserById(Integer id) {
        return users.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    @Override
    public User updateUser(User newUser, Integer id) {

        if (users.get(id) == null) {
            log.info("user not found");
            throw new NotFoundException("user not found");
        }

        User oldUser = users.get(id);

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

        if (newUser.getEmail() != null) {
            if (emailUniqueSet.contains(newUser.getEmail())) {
                throw new ConflictException("user with this email al-ready exist");
            }
            oldUser.setEmail(newUser.getEmail());
        }

        log.info("user with id={} updated", id);
        return users.get(id);
    }

    @Override
    public User deleteUser(Integer userId) {
        User user = findUserById(userId);
        log.info("Successfully user with {} deleted", userId);
        return users.remove(user.getId());
    }

    private int hasNextUserId() {
        return userIdAtMoment++;
    }
}
