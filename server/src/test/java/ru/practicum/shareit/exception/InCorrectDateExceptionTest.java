package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.controller.ErrorHandler;
import ru.practicum.shareit.controller.ErrorResponse;
import ru.practicum.shareit.exceptions.InCorrectDateException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest
@ContextConfiguration(classes = {ErrorHandler.class, ErrorHandler.class})
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
