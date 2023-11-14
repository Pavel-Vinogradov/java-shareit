package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExist;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userRepository;

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUser(Long userId) {
        if (userRepository.isUserExistsById(userId)) {
            throw new NotFoundException("User не найден");
        }
        return userRepository.getUser(userId);
    }

    public User addUser(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Invalid user body. Incorrect email.");
        }
        if (userRepository.isUserExistsByEmail(user.getEmail())) {
            throw new UserAlreadyExist("User  уже существует.");
        }
        return userRepository.addUser(user);
    }

    public User updateUser(Long userId, User user) {
        if (userRepository.isUserExistsById(userId)) {
            throw new NotFoundException("User не найден");
        }
        return userRepository.updateUser(userId, user);
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.isUserExistsById(userId)) {
            throw new NotFoundException("User не найден");
        }
        return userRepository.deleteUser(userId);
    }
}
