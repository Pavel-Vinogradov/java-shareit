package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.controller.ErrorResponse;
import ru.practicum.shareit.exceptions.ConflictException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class ConflictExceptionTest {
    @Test
    public void shouldConflictExceptionTest() {
        ErrorResponse errorResponse = new ErrorResponse("Пользователь с таким email уже существует.",
                "Пользователь с таким email уже существует.");
        ConflictException exception = new ConflictException(
                "Пользователь с таким email уже существует.");

        assertNotNull(errorResponse);
        assertNotNull(exception);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
        assertEquals(errorResponse.getError(), exception.getMessage());

    }
}
