package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.controller.ErrorHandler;
import ru.practicum.shareit.controller.ErrorResponse;
import ru.practicum.shareit.exceptions.UnsupportedStateException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest
@ContextConfiguration(classes = {ErrorHandler.class, ErrorHandler.class})
public class UnsupportedStateExceptionTest {
    @Test
    public void shouldUnsupportedStateExceptionTest() {
        ErrorResponse errorResponse = new ErrorResponse("Неверный статус бронирования.",
                "Неверный статус бронирования.");
        UnsupportedStateException exception = new UnsupportedStateException(
                "Неверный статус бронирования.");

        assertNotNull(errorResponse);
        assertNotNull(exception);
        assertEquals(errorResponse.getDescription(), exception.getMessage());
        assertEquals(errorResponse.getError(), exception.getMessage());
    }
}
