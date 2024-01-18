package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class RequestDtoTest {
    private RequestDto requestDto;

    @BeforeEach
    public void setUp() {
        requestDto = new RequestDto();
    }

    @Test
    public void testSetAndGetId() {
        Long id = 1L;
        requestDto.setId(id);
        assertEquals(id, requestDto.getId());
    }

    @Test
    public void testSetAndGetDescription() {
        String description = "This is a test description";
        requestDto.setDescription(description);
        assertEquals(description, requestDto.getDescription());
    }


    @Test
    public void testSetAndGetCreatedTime() {
        LocalDateTime created = LocalDateTime.now();
        requestDto.setCreated(created);
        assertEquals(created, requestDto.getCreated());
    }

}
