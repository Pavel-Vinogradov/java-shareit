package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.controller.ErrorResponse;
import ru.practicum.shareit.exceptions.InCorrectDateException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class InCorrectDateExceptionTest {
    @Test
    public void shouldInCorrectDateExceptionTest() {
        ErrorResponse errorResponse = new ErrorResponse("Некорректная дата бронирования.",
                "Некорректная дата бронирования.");
        InCorrectDateException exception = new InCorrectDateException(
                "Некорректная дата бронирования.");

        assertNotNull(errorResponse);
        assertNotNull(exception);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
        assertEquals(errorResponse.getError(), exception.getMessage());
    }
}
