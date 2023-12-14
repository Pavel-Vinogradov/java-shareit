package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.request.mappers.RequestMapper.toItemRequestDto;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final ModelMapper mapper = new ModelMapper();
    @InjectMocks
    private ItemRequestServiceIml itemRequestService;
    private User user;

    private Request request;


    @BeforeEach
    void setUp() {


        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Щётка для обуви")
                .description("Стандартная щётка для обуви")
                .available(true)
                .request(1L)
                .ownerId(user.getId())
                .comments(new ArrayList<>())
                .build();
        request = Request.builder()
                .id(1L)
                .created(LocalDateTime.of(2023, 7, 9, 13, 56))
                .description("Хотел бы воспользоваться щёткой для обуви")
                .requestor(user)
                .items(Collections.singletonList(item))
                .build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addItemRequestServiceTest() {
        RequestDto requestDto1 = mapper.map(request, RequestDto.class);

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.addItemRequest(requestDto1, 1L));
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any()))
                .thenReturn(request);
        RequestDto requestDto2 = itemRequestService.addItemRequest(requestDto1, 1L);
        assertEquals(requestDto2, toItemRequestDto(request));
    }

    @Test
    void addItemRequestWithWrongIdTest() {
        when(userRepository.findById(77L))
                .thenThrow(new UserNotFoundException("Пользователь с id = 77 не найден"));
        var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.addItemRequest(mapper.map(request, RequestDto.class), 77L));

        assertEquals("Пользователь с id = 77 не найден", exception.getMessage());
    }

    @Test
    void getItemRequestTest() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequest(1L));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorId(anyLong()))
                .thenReturn(List.of(request));
        List<RequestDtoWithRequest> requestList = itemRequestService.getItemRequest(1L);
        RequestDtoWithRequest requestDto1 = mapper.map(request, RequestDtoWithRequest.class);
        assertEquals(requestList, List.of(requestDto1));
    }


    @Test
    void getItemRequestWithWrongUserIdTest() {
        when(userRepository.findById(77L))
                .thenThrow(new RequestNotFoundException("Пользователь с id = 77 не найден"));
        var exception = assertThrows(
                RequestNotFoundException.class,
                () -> itemRequestService.getItemRequest(77L));

        assertEquals("Пользователь с id = 77 не найден", exception.getMessage());

    }

    @Test
    void getAllItemRequestTest() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllItemRequest(1L, 1, 1));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdIsNot(anyLong(), any()))
                .thenReturn(List.of(request));
        List<RequestDtoWithRequest> requestList = itemRequestService.getAllItemRequest(1L, 1, 1);
        RequestDtoWithRequest requestDto1 = mapper.map(request, RequestDtoWithRequest.class);
        assertEquals(requestList, List.of(requestDto1));
    }


    @Test
    void getAllItemRequestWithWrongRequestIdTest() {

        var exception = assertThrows(
                BadRequestException.class,
                () -> itemRequestService.getAllItemRequest(1L, -1, 1));
        assertEquals("400 BAD_REQUEST \"неверный параметр пагинации\"", exception.getMessage());
    }

    @Test
    void getAllItemRequestWithWrongUserIdTest() {
        when(userRepository.findById(77L))
                .thenThrow(new RequestNotFoundException("Пользователь с id = 77 не найден"));
        var exception = assertThrows(
                RequestNotFoundException.class,
                () -> itemRequestService.getAllItemRequest(77L, 1, 1));

        assertEquals("Пользователь с id = 77 не найден", exception.getMessage());

    }

    @Test
    void getRequestByIdTest() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        assertThrows(RequestNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));
        RequestDtoWithRequest requestById = itemRequestService.getRequestById(1L, 1L);

        RequestDtoWithRequest requestDto1 = mapper.map(request, RequestDtoWithRequest.class);

        assertEquals(requestById, requestDto1);
    }

    @Test
    void getRequestByIdWithWrongRequestIdTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.findById(66L))
                .thenThrow(new RequestNotFoundException("Запрос с id = 66 не найден"));

        var exception = assertThrows(
                RequestNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 66L));
        assertEquals("Запрос с id = 66 не найден", exception.getMessage());
    }

    @Test
    void getRequestByWrongUserIdTest() {
        when(userRepository.findById(77L))
                .thenThrow(new RequestNotFoundException("Пользователь с id = 77 не найден"));
        var exception = assertThrows(
                RequestNotFoundException.class,
                () -> itemRequestService.getRequestById(77L, 1L));

        assertEquals("Пользователь с id = 77 не найден", exception.getMessage());
    }
}
