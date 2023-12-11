package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(long userId, BookingDto bookingDtoItem);

    BookingDto confirmOrCancelBooking(long userId, Long bookingId, boolean approved);

    BookingDto getBookingForOwnerOrBooker(long userId, Long bookingId);

    List<BookingDto> getBooking(long userId, String stateParam);

    List<BookingDto> getOwnerBooking(long userId, String stateParam);
}
