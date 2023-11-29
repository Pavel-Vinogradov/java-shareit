package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public BookingDto saveBooking(long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден " + userId));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException("Предмет не найден."));

        Booking booking = toBooking(user, item, bookingDto);

        if (!booking.getEnd().isAfter(booking.getStart())) {
            log.debug("Некорректная дата бронирования");
            throw new InCorrectDateException("Некорректная дата бронирования");
        }

        if ((booking.getItem().getOwnerId()).equals(booking.getBooker().getId())) {
            log.debug("Попытка бронирования своего же предмета отклонена");
            throw new ItemNotFoundException("Бронь своего предмета: " + booking.getItem());
        }

        if (!booking.getItem().getAvailable()) {
            log.debug("В данный момент предмет: {} не может быть забронирован. ", booking.getItem());
            throw new ItemUnavailableException("Предмет недоступен для аренды в данный момент: "
                    + booking.getItem());
        }
        if (booking.getStatus() == null) {
            booking.setStatus(Status.WAITING);
        }
        log.info("Предмет {} ожидает подтверждения бронирования от владельца: {} ",
                booking.getItem(), booking.getItem().getOwnerId());

        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto confirmOrCancelBooking(long userId, Long bookingId, boolean approved) {

        if (!userRepository.existsById(userId)) {
            log.debug("Пользователь {} не найден ", userId);
            throw new UserNotFoundException("Пользователь не найден " + userId);
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));


        if (booking.getStatus() == Status.APPROVED) {
            log.debug("Бронирование уже подтверждено ранее");
            throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        if (booking.getItem().getOwnerId() != userId) {
            log.debug("Подтвердить/отклонить бронирование может только владелец предмета");
            throw new BookingNotFoundException("Подтвердить/отклонить бронирование может только " +
                    "владелец предмета - " + userId);
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto getBookingForOwnerOrBooker(long userId, Long bookingId) {

        if (!userRepository.existsById(userId)) {
            log.debug("Пользователь {} не найден ", userId);
            throw new UserNotFoundException("Пользователь не найден " + userId);
        }

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));


        if (booking.getItem().getOwnerId() != userId && booking.getBooker().getId() != userId) {
            log.debug("Просмотр бронирования доступен только владельцу предмета или его арендатору");
            throw new BookingNotFoundException("Просмотр бронирования доступен только владельцу предмета" +
                    " или его арендатору");
        }

        if (!booking.getItem().getAvailable()) {
            log.debug("Предмет недоступен для бронирования в данный момент");
            throw new ItemUnavailableException("Предмет недоступен для бронирования в данный момент");
        }

        return toBookingDto(booking);
    }


    @Override
    public List<BookingDto> getBooking(long userId, String stateParam) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Collection<Booking> bookingList = new ArrayList<>();
        LocalDateTime timeNow = LocalDateTime.now();
        if (!userRepository.existsById(userId)) {
            log.debug("Пользователь {} не найден ", userId);
            throw new UserNotFoundException("Пользователь не найден " + userId);
        }

        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, timeNow, sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, timeNow, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, timeNow, timeNow, sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED);
                break;
        }
        return bookingList.isEmpty() ? Collections.emptyList() : bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBooking(long userId, String stateParam) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Collection<Booking> bookingList = new ArrayList<>();
        LocalDateTime timeNow = LocalDateTime.now();
        if (!userRepository.existsById(userId)) {
            log.debug("Пользователь {} не найден ", userId);
            throw new UserNotFoundException("Пользователь не найден " + userId);
        }

        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItem_OwnerIdAndEndIsBefore(userId, timeNow, sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItem_OwnerIdAndStartIsAfter(userId, timeNow, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(userId, timeNow, timeNow, sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItem_OwnerIdAndStatus(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItem_OwnerIdAndStatus(userId, Status.REJECTED);
                break;
        }
        return bookingList.isEmpty() ? Collections.emptyList() : bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}