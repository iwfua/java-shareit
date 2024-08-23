package ru.practicum.shareit.user.repository;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final Map<Integer, User> users;
    private int userIdAtMoment;

    public UserRepositoryImpl() {
        this.users = new HashMap<>();
    }

    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("user al-ready exist");
        }

        int userId = hasNextUserId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Successfully user with {} created", userId);
        return users.get(userId);
    }

    public User findUserById(Integer id) {
        return users.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    public Boolean existUserWithEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public User updateUser(User user, Integer id) {

        if (users.get(id) == null) {
            log.info("user not found");
            throw new NotFoundException("user not found");
        }

        user.setId(id);
        users.put(id, user);
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
