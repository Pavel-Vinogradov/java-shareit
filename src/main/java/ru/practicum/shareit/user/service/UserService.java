package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    void deleteUser(long userId);

    UserDto updateUser(UserDto userDto, long userId);

    UserDto getUserById(long id);

}
