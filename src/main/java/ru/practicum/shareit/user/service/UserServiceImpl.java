package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Получен список всех пользователей");
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Создан пользователь, id = {} ", userDto);
        User user = toUser(userDto);
        return toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(long id) {
        log.info("Удалён пользователь, id = {} ", id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User user = toUserWithId(userId, userDto);
        User oldUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден " + user.getId()));
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        log.info("Данные пользователя {} обновлены ", user);
        return toUserDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден " + id));
        log.info("Получен пользователь, id = {} ", id);
        return toUserDto(user);
    }
}
