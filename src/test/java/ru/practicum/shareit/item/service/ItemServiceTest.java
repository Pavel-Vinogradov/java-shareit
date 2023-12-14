package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.InCorrectBookingException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoShort;
import static ru.practicum.shareit.comment.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private final ModelMapper mapper = new ModelMapper();
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user;

    private Comment comment;
    private Booking booking;
    private Item item;
    private Request request;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();


        comment = Comment.builder()
                .id(1L)
                .text("Комментарий")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.of(2024, 7, 9, 13, 56))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        request = Request.builder()
                .id(1L)
                .requestor(user)
                .description("user2")
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .name("Щётка для обуви")
                .description("Стандартная щётка для обуви")
                .available(true)
                .request(request.getId())
                .ownerId(user.getId())
                .comments(new ArrayList<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
    }

    @Test
    void saveItemTest() {
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest())
                .build();

        assertEquals(toItemDto(item), itemService.saveItem(itemDto, user.getId()));
    }


    @Test
    void saveItemWithEmptyNameTest() {
        item.setName("");
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(user.getId())
                .available(item.getAvailable())
                .requestId(item.getRequest())
                .build();
        var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.saveItem(itemDto, 999L));

        assertEquals("Пользователь не найден 999", exception.getMessage());
    }

    @Test
    void saveItemWithEmptyAvailableTest() {
        item.setAvailable(null);
        ItemDto itemDto = mapper.map(item, ItemDto.class);
        var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.saveItem(itemDto, 999L));

        assertEquals("Пользователь не найден 999", exception.getMessage());
    }

    @Test
    void saveItemWithWrongUserIDTest() {
        when(userRepository.findById(77L))
                .thenThrow(new ItemNotFoundException("Пользователь с id=77 не найден"));
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(user.getId())
                .available(item.getAvailable())
                .requestId(item.getRequest())
                .build();
        var exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.saveItem(itemDto, 77L));

        assertEquals("Пользователь с id=77 не найден", exception.getMessage());
    }

    @Test
    void updateItemTest() {
        ItemDto itemDto = mapper.map(item, ItemDto.class);
        assertThrows(
                ItemNotFoundException.class,
                () -> itemService.updateItem(itemDto, user.getId())
        );
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any()))
                .thenReturn(item);

        item.setName("Щётка для обуви updated");
        item.setDescription("Стандартная щётка для обуви updated");
        ItemDto updatedItem = itemService.updateItem(itemDto, user.getId());
        assertEquals(updatedItem, toItemDto(item));

    }

    @Test
    void searchItemNullTest() {

        assertEquals(List.of(), itemService.searchItem("".toLowerCase(), 0, 20));
        assertEquals(List.of(), itemService.searchItem(" ", 0, 20));
        assertThrows(BadRequestException.class, () -> itemService.searchItem("1".toLowerCase(), -1, -1));
        assertThrows(BadRequestException.class, () -> itemService.searchItem("1".toLowerCase(), 0, 0));
    }

    @Test
    void postCommentTest() {
        booking.setBooker(user);
        item.setBookings(Collections.singletonList(booking));
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.save(any())).thenReturn(comment);


        CommentDto createCommentDto = itemService.postComment(user.getId(), item.getId(), toCommentDto(comment));

        assertEquals(createCommentDto, toCommentDto(comment));
    }

    @Test
    void postCommentWrongUserTest() {
        booking.setBooker(user);
        item.setBookings(Collections.singletonList(booking));

        CommentDto commentDto = mapper.map(comment, CommentDto.class);

        var exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.postComment(1L, 1L, commentDto));

        assertEquals("Пользователь не найден 1", exception.getMessage());
    }

    @Test
    void postCommentFromWrongItemTest() {

        CommentDto commentDto = mapper.map(comment, CommentDto.class);
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        var exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.postComment(1L, 1L, commentDto));

        assertEquals("Предмет не найден 1", exception.getMessage());
    }

    @Test
    void addEmptyCommentTest() {

        CommentDto commentDto = mapper.map(comment, CommentDto.class);
        commentDto.setText("");

        var exception = assertThrows(
                InCorrectBookingException.class,
                () -> itemService.postComment(1L, 1L, commentDto));

        assertEquals("Комментарий не может быть пустым", exception.getMessage());
    }

    @Test
    void postCommentWithBlankContentThrowsBadRequestException() {

        long userId = 1L;
        long itemId = 2L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        assertThrows(InCorrectBookingException.class, () -> itemService.postComment(userId, itemId, commentDto));
    }

    @Test
    void getItemShouldThrowItemNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(1L, 2L));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto expected = toItemDto(item);
        expected.setComments(new ArrayList<>()); // Список пустой, а не null
        ItemDto result = itemService.getItemById(1L, 1L);
        assertEquals(expected, result);

        when(bookingRepository.findFirstBookingByItemIdAndStartIsAfterAndStatusNotLikeOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(booking);
        ItemDto result1 = itemService.getItemById(1L, 1L);
        expected.setNextBooking(toBookingDtoShort(booking));
        assertEquals(expected, result1);
        when(bookingRepository.findFirstBookingByItemIdAndStartIsBeforeAndStatusNotLikeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(booking);
        ItemDto result2 = itemService.getItemById(1L, 1L);
        expected.setLastBooking(toBookingDtoShort(booking));
        assertEquals(expected, result2);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        ItemDto result3 = itemService.getItemById(1L, 1L);
        CommentDto commentDto = toCommentDto(comment);
        expected.setComments(List.of(commentDto));
        assertEquals(expected, result3);

    }

    @Test
    void deleteItem() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(999L)).thenReturn(false);
        assertThrows(ItemNotFoundException.class, () -> itemService.deleteItemById(999L));
        itemService.deleteItemById(1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void getItemsByUserExceptionTest() {

        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.getItemsByUser(1L, 0, 20));

        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> itemService.getItemsByUser(1L, -1, -1));
        assertThrows(BadRequestException.class, () -> itemService.getItemsByUser(1L, 0, 0));

        when(itemRepository.findAllByOwnerId(eq(1L), any())).thenReturn(null);
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemsByUser(1L, 0, 20));

        when(itemRepository.findAllByOwnerId(eq(1L), any())).thenReturn(List.of());
        List<ItemDto> result = itemService.getItemsByUser(1L, 0, 20);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        when(itemRepository.findAllByOwnerId(eq(1L), any())).thenReturn(List.of(item));
        List<ItemDto> result1 = itemService.getItemsByUser(1L, 0, 20);
        assertNotNull(result1);
        assertFalse(result1.isEmpty());
    }
}
