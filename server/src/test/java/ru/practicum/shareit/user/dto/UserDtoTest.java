package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserDtoTest {
    @Test
    public void createUserDtoTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Raisa")
                .email("raisa@mail.ru")
                .build();

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("Raisa", userDto.getName());
        assertEquals("raisa@mail.ru", userDto.getEmail());
    }

    @Test
    public void testEquals() {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();


        UserDto user3 = UserDto.builder()
                .id(3L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        assertNotEquals(user1, user3);
    }

    @Test
    public void testHashCode() {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        UserDto user3 = UserDto.builder()
                .id(3L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
}
