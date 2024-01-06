package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private Booking booking;
    private Item item;
    private User user;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("itemDescription")
                .available(true)
                .ownerId(User.builder()
                        .id(2L)
                        .build().getId())
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMonths(1))
                .end(LocalDateTime.of(2024, 7, 9, 13, 56))
                .booker(toUserDto(user))
                .itemId(2L)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void saveBooking() {

        BookingDto bookingDto = toBookingDto(booking);
        assertThrows(UserNotFoundException.class, () -> {
            BookingDto exceptionBooking = toBookingDto(booking);
            exceptionBooking.setStart(LocalDateTime.of(2050, Month.FEBRUARY, 25, 10, 0));
            bookingService.saveBooking(1L, exceptionBooking);
        });
        when(userRepository.findById(bookingDto.getBooker().getId())).thenReturn(Optional.of(user));
        assertThrows(ItemNotFoundException.class, () -> {
            BookingDto exceptionBooking = toBookingDto(booking);
            exceptionBooking.setStart(LocalDateTime.of(2050, Month.FEBRUARY, 25, 10, 0));
            bookingService.saveBooking(1L, exceptionBooking);
        });
        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingService.saveBooking(1L, bookingDto))
                .thenReturn(toBookingDto(booking));

        assertThrows(InCorrectDateException.class, () -> {
            BookingDto exceptionBooking = toBookingDto(booking);
            exceptionBooking.setStart(LocalDateTime.of(2050, Month.FEBRUARY, 25, 10, 0));
            bookingService.saveBooking(1L, exceptionBooking);
        });

        assertThrows(ItemNotFoundException.class, () -> {
            item.setOwnerId(user.getId());
            bookingService.saveBooking(1L, bookingDto);
        });

        when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(
                Item.builder()
                        .id(1L)
                        .name("item")
                        .description("itemDescription")
                        .available(false)
                        .ownerId(User.builder()
                                .id(2L)
                                .build().getId())
                        .build()));

        assertThrows(ItemUnavailableException.class, () -> bookingService.saveBooking(1L, bookingDto));
    }

    @Test
    void saveBookingTest() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(any()))
                .thenReturn(booking);
        BookingDto bookingDto5 = bookingService.saveBooking(user.getId(), bookingDto);

        assertEquals(1, bookingDto5.getId());
    }

    @Test
    void confirmOrCancelBooking() {
        item.setOwnerId(user.getId());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(UserNotFoundException.class, () -> {
            booking.setStatus(Status.APPROVED);
            bookingService.confirmOrCancelBooking(1L, 1L, true);
        });

        assertThrows(UserNotFoundException.class, () -> {
            item.setOwnerId(User.builder()
                    .id(2L)
                    .build().getId());
            bookingService.confirmOrCancelBooking(1L, 1L, true);
        });

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingService.confirmOrCancelBooking(1L, 1L, true));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingService.confirmOrCancelBooking(1L, 1L, true));
    }

    @Test
    void bookingNotFoundExceptionForNonExistBooking() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.confirmOrCancelBooking(1L, 1L, true));
    }

    @Test
    void unsupportedStateExceptionForAlreadyApprovedBooking() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(Status.APPROVED);

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.confirmOrCancelBooking(1L, 1L, true));
    }

    @Test
    void bookingNotFoundExceptionForNonOwnerUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Item item = new Item();
        item.setOwnerId(2L);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.confirmOrCancelBooking(1L, 1L, true));
    }

    @Test
    void getBookingForOwnerOrBooker() {
        Booking bookingTest = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, Month.MAY, 25, 12, 0))
                .end(LocalDateTime.of(2023, Month.MAY, 26, 12, 0))
                .item(item)
                .booker(user)
                .status(Status.REJECTED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingTest));
        when(userRepository.findById(bookingTest.getBooker().getId())).thenReturn(Optional.of(user));
        assertThrows(UserNotFoundException.class, () -> bookingService.getBookingForOwnerOrBooker(1L, 1L));

    }

    @Test
    void whenBookingNotFound_thenThrowBookingNotFoundException() {
        long userId = 2L;
        long bookingId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingForOwnerOrBooker(userId, bookingId));
    }

    @Test
    void whenUserIsNotOwnerOrBooker_thenThrowBookingNotFoundException() {
        long userId = 2L;
        long bookingId = 1L;
        User differentUser = new User();
        differentUser.setId(3L);
        Item someItem = new Item();
        someItem.setId(1L);
        someItem.setOwnerId(differentUser.getId());

        Booking bookingTest = Booking.builder()
                .id(bookingId)
                .item(someItem)
                .booker(differentUser)
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingTest));

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingForOwnerOrBooker(userId, bookingId));
    }


    @Test
    void getAllBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getBooking(
                user.getId(), State.ALL.name(), 0, 20));
    }

    @Test
    void getPastBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getBooking(
                user.getId(), State.PAST.name(), 0, 20));
    }

    @Test
    void getFutureBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getBooking(
                user.getId(), State.FUTURE.name(), 0, 20));
    }

    @Test
    void getCurrentBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getBooking(
                user.getId(), State.CURRENT.name(), 0, 20));
    }

    @Test
    void getWaitingBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getBooking(
                user.getId(), State.WAITING.name(), 0, 20));
    }

    @Test
    void getRejectBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getBooking(
                user.getId(), State.REJECTED.name(), 0, 20));
    }

    @Test
    void getAllOwnerBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getOwnerBooking(
                user.getId(), State.ALL.name(), 0, 20));
    }

    @Test
    void getPastOwnerBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByItem_OwnerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getOwnerBooking(
                user.getId(), State.PAST.name(), 0, 20));
    }

    @Test
    void getFutureOwnerBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByItem_OwnerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getOwnerBooking(
                user.getId(), State.FUTURE.name(), 0, 20));
    }

    @Test
    void getCurrentOwnerBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getOwnerBooking(
                user.getId(), State.CURRENT.name(), 0, 20));
    }

    @Test
    void getWaitingOwnerBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByItem_OwnerIdAndStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getOwnerBooking(
                user.getId(), State.WAITING.name(), 0, 20));
    }

    @Test
    void getRejectOwnerBookingTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        when(bookingRepository.findAllByItem_OwnerIdAndStatus(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> expectedResult = List.of(toBookingDto(booking));

        assertEquals(expectedResult, bookingService.getOwnerBooking(
                user.getId(), State.REJECTED.name(), 0, 20));
    }

    @Test
    void getBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBooking(user.getId(), "TEST", 0, 10));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBooking(user.getId(), "TEST", 0, -10));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBooking(user.getId(), "TEST", -1, 10));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingService.getBooking(user.getId(), "ALL", 0, 10));

    }

    @Test
    void getByBookerTest() {
        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        booking.setItem(item);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        BookingDto booking1 = bookingService.getBookingForOwnerOrBooker(user.getId(), booking.getId());

        assertEquals(toBookingDto(booking), booking1);
    }

    @Test
    void getOwnerBooking() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.getOwnerBooking(user.getId(), "TEST", 0, 10));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getOwnerBooking(user.getId(), "TEST", 0, -10));

        assertThrows(IllegalArgumentException.class, () -> bookingService.getOwnerBooking(user.getId(), "TEST", -1, 10));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.getOwnerBooking(user.getId(), "ALL", 0, 10));
    }

    @Test
    void confirmOrCancelBookingTest() {
        Item item2 = Item.builder()
                .id(2L)
                .name("Щётка для ванны")
                .description("Стандартная щётка для ванны")
                .ownerId(user.getId())
                .available(true)
                .request(2L)
                .build();

        when(userRepository.existsById(any()))
                .thenReturn(true);
        Booking booking = toBooking(user, item, bookingDto);
        booking.setItem(item2);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto booking1 = bookingService.confirmOrCancelBooking(user.getId(), 3L, true);

        assertEquals(toBookingDto(booking), booking1);
    }
}