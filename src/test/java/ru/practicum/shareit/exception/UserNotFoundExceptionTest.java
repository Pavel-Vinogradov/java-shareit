package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.controller.ErrorResponse;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserNotFoundExceptionTest {
    @Test
    public void shouldUserNotFoundExceptionTest() {
        ErrorResponse errorResponse = new ErrorResponse("Пользователь не найден.",
                "Пользователь не найден.");
        UserNotFoundException exception = new UserNotFoundException(
                "Пользователь не найден.");

        assertNotNull(errorResponse);
        assertNotNull(exception);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
        assertEquals(errorResponse.getError(), exception.getMessage());
    }
}
