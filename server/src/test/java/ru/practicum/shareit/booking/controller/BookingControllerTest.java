package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.controller.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;

@WebMvcTest
@ContextConfiguration(classes = {BookingController.class, ErrorHandler.class})
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private User user;
    private ItemDto itemDto;
    private static final String USERID_HEADER = "X-Sharer-User-Id";


    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();
        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Щётка для обуви")
                .description("Стандартная щётка для обуви")
                .available(true)
                .requestId(1L)
                .ownerId(1L)
                .build();


        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 7, 9, 13, 56))
                .end(LocalDateTime.of(2024, 7, 9, 13, 56))
                .itemId(1L)
                .booker(userDto)
                .item(itemDto)
                .status(Status.WAITING)
                .build();
    }


    @SneakyThrows
    @Test
    void confirmOrCancelBooking() {
        Booking booking = toBooking(user, toItem(user, itemDto), bookingDto);
        when(bookingService.confirmOrCancelBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(toBookingDto(booking));
        BookingDto bookingDto1 = toBookingDto(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USERID_HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))

                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }

    @Test
    void getBookingForOwnerOrBooker() throws Exception {
        Booking booking = toBooking(user, toItem(user, itemDto), bookingDto);
        when(bookingService.getBookingForOwnerOrBooker(anyLong(), anyLong()))
                .thenReturn(toBookingDto(booking));

        BookingDto bookingDto1 = toBookingDto(booking);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USERID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))

                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }

    @Test
    void getAllBookingsForBooker() throws Exception {
        Booking booking = toBooking(user, toItem(user, itemDto), bookingDto);
        when(bookingService.getBooking(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(toBookingDto(booking)));

        mockMvc.perform(get("/bookings" + "?state=ALL&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(toBookingDto(booking)))));

        mockMvc.perform(get("/bookings" + "?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsForOwner() throws Exception {
        Booking booking = toBooking(user, toItem(user, itemDto), bookingDto);

        when(bookingService.getOwnerBooking(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(toBookingDto(booking)));

        mockMvc.perform(get("/bookings" + "/owner?state=ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 10L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(toBookingDto(booking)))));

        mockMvc.perform(get("/bookings" + "/owner?state=ALL&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USERID_HEADER, 10L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addBookingIsOkTest() throws Exception {
        when(bookingService.saveBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        String requestBody = mapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(requestBody)
                        .header(USERID_HEADER, 10)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

    }
}