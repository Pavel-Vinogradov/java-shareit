package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    private final ModelMapper mapper = new ModelMapper(); //maybe final
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void saveUserTest() {
        when(userRepository.save(any()))
                .thenReturn(user);
        UserDto userSaved = userService.saveUser(toUserDto(user));

        assertNotNull(userSaved);
        assertEquals(userSaved, toUserDto(user));

    }

    @Test
    void saveUserWithDoubleExceptionTest() {

        Mockito.when(userRepository.save(any()))
                .thenReturn(user)
                .thenThrow(new ConflictException("Пользователь с таким email уже существует"));

        UserDto userSaved = userService.saveUser(toUserDto(user));

        assertNotNull(userSaved);
        assertThrows(
                ConflictException.class,
                () -> userService.saveUser(toUserDto(user))
        );
    }


    @Test
    void getAllUsersTest() {

        when(userRepository.findAll())
                .thenReturn(List.of(user));
        Collection<UserDto> expectedResult = List.of(toUserDto(user));

        assertEquals(expectedResult, userService.getAllUsers());
    }

    @Test
    void updateUserNameAndEmailTest() {
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(toUserDto(user), user.getId()));
        user.setEmail("ivan@mailupdated.ru");
        user.setName("ivanupdated");

        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        UserDto content = userService.updateUser(toUserDto(user), user.getId());

        assertEquals(content, toUserDto(user));
    }

    @Test
    void deleteUserServiceTest() {
        when(userRepository.save(any()))
                .thenReturn(user);
        UserDto userDto = mapper.map(user, UserDto.class);
        assertEquals(userDto.getEmail(), userService.saveUser(toUserDto(user)).getEmail());

        assertThrows(IndexOutOfBoundsException.class,
                () -> userRepository.findAll().get(0));
    }

    @Test
    void getUserByIdTest() {
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(user.getId()));
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        userService.getUserById(1L);
        assertEquals(toUserDto(user), userService.getUserById(1L));
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);

        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(999L));
    }
}
