package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDtoReq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class RequestDtoWithRequestTest {
    @Test
    public void testEqualsAndHashCode() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<ItemDtoReq> items1 = new ArrayList<>();
        items1.add(ItemDtoReq.builder()
                .id(1L)
                .name("Щётка для обуви")
                .description("Стандартная щётка для обуви")
                .available(true)
                .requestId(1L)
                .ownerId(1L)
                .build());


        List<ItemDtoReq> items2 = new ArrayList<>();
        items2.add(ItemDtoReq.builder()
                .id(2L)
                .name("Щётка для обуви2")
                .description("Стандартная щётка для обуви2")
                .available(true)
                .requestId(2L)
                .ownerId(2L).build());

        RequestDtoWithRequest request1 = new RequestDtoWithRequest(1L, "Request 1", currentTime, items1);
        RequestDtoWithRequest request2 = new RequestDtoWithRequest(2L, "Request 2", currentTime, items2);
        RequestDtoWithRequest request3 = new RequestDtoWithRequest(1L, "Request 1", currentTime, items1);

        assertNotEquals(request1, request2);
        assertEquals(request1, request3);

        int hashCode1 = request1.hashCode();
        int hashCode2 = request2.hashCode();
        int hashCode3 = request3.hashCode();

        assertNotEquals(hashCode1, hashCode2);
        assertEquals(hashCode1, hashCode3);
    }
}
