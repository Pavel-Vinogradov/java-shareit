package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exceptions.BadRequestException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BadRequestExceptionTest {
    @Test
    public void shouldBadRequestExceptionTest() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Bad Request";

        BadRequestException badRequestException = new BadRequestException(httpStatus, message);

        assertEquals(httpStatus, badRequestException.getStatus());
        assertEquals(message, badRequestException.getReason());
    }

    @Test
    public void testBadRequestExceptionWithCustomHttpStatus() {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // Custom HTTP Status
        String message = "Internal Server Error";

        BadRequestException badRequestException = new BadRequestException(httpStatus, message);

        assertEquals(httpStatus, badRequestException.getStatus());
        assertEquals(message, badRequestException.getReason());
    }
}
