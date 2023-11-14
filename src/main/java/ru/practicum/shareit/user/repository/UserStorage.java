package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUser(long userId);

    User addUser(User user);

    User updateUser(long userId, User user);

    boolean deleteUser(long userId);

    boolean isUserExistsById(long userId);

    boolean isUserExistsByEmail(String email);

}
