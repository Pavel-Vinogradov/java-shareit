package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.InCorrectBookingException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoShort;
import static ru.practicum.shareit.comment.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден " + userId));
        Item item = toItem(user, itemDto);
        log.info("Добавлен предмет {}, владелец: id = {}", itemDto, userId);
        item.setOwnerId(userId);
        item.setRequest(itemDto.getRequestId());
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId) {
        Item updatedItem = itemRepository.findById(itemDto.getId()).orElseThrow(() ->
                new ItemNotFoundException("Предмет не найден " + itemDto.getId()));
        if (!Objects.equals(updatedItem.getOwnerId(), userId)) {
            throw new ItemNotFoundException("Item not belongs to this user.");
        }
        Item savedItem = itemUpdate(updatedItem, itemDto);
        log.info("Выполнено обновление информации о предмете = {}, " +
                "принадлежащем пользователю, id = {}", updatedItem.getId(), userId);
        itemRepository.save(savedItem);
        return toItemDto(savedItem);
    }

    private Item itemUpdate(Item updatedItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwnerId() != null) {
            updatedItem.setOwnerId(userRepository.findById(itemDto.getOwnerId())
                    .orElseThrow(() -> new UserNotFoundException("User not found.")).getId());
        }
        return updatedItem;
    }


    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Предмет не найден " + itemId));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        ItemDto itemDto = toItemDto(item);
        if (item.getOwnerId().equals(userId)) {
            addLastAndNextDateTimeForBookingToItem(itemDto);
        }
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        log.info("Получен предмет, id = {}", itemId);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            log.debug("Пользователь {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден " + userId);
        }
        Pageable pageable = PageRequest.of(from / size, size);

        if (itemRepository.findAllByOwnerId(userId, pageable) == null) {
            log.info("У пользователя {} нет предметов для аренды ", userId);
            throw new ItemNotFoundException("У пользователя нет предметов для аренды " + userId);
        }
        List<ItemDto> itemsList = itemRepository.findAllByOwnerId(userId, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemsList.forEach(this::addLastAndNextDateTimeForBookingToItem);
        log.info("Список всех предметов, принадлежащих пользователю, id = {}", userId);
        return itemsList;
    }

    @Override
    public Collection<ItemDto> searchItem(String text, int from, int size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("Выполнен поиск среди предметов по : {}.", text);
        return itemRepository.searchItem(text, PageRequest.of(from / size, size))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            log.debug("Комментарий не может быть пустым");
            throw new InCorrectBookingException("Комментарий не может быть пустым");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден " + itemId));
        List<Booking> bookings = item.getBookings();

        if (bookings
                .stream().noneMatch(booking -> (booking.getBooker().getId() == userId)
                        && !booking.getStart().isAfter(LocalDateTime.now())
                        && !booking.getStatus().equals(Status.REJECTED))) {
            throw new ItemUnavailableException(
                    "Комментировать может только арендатор предмета, с наступившим началом времени бронирования " +
                            "и статусом НЕ REJECTED");
        }

        LocalDateTime saveTime = LocalDateTime.now();

        Comment comment = CommentMapper.toComment(user, item, commentDto, saveTime);
        comment.setItem(item);
        comment.setCreated(saveTime);
        commentRepository.save(comment);

        return toCommentDto(comment);
    }

    @Override
    public void deleteItemById(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Item not found.");
        }
        itemRepository.deleteById(itemId);
        log.info("Удален предмет {}", itemId);
    }

    private void addLastAndNextDateTimeForBookingToItem(ItemDto itemDto) {
        LocalDateTime timeNow = LocalDateTime.now();

        Booking next = bookingRepository
                .findFirstBookingByItemIdAndStartIsAfterAndStatusNotLikeOrderByStartAsc(itemDto.getId(),
                        timeNow, Status.REJECTED);
        Booking last = bookingRepository
                .findFirstBookingByItemIdAndStartIsBeforeAndStatusNotLikeOrderByStartDesc(itemDto.getId(),
                        timeNow, Status.REJECTED);

        if (next != null) {
            itemDto.setNextBooking(toBookingDtoShort(next));
        } else {
            itemDto.setNextBooking(null);
        }

        if (last != null) {
            itemDto.setLastBooking(toBookingDtoShort(last));
        } else {
            itemDto.setLastBooking(null);
        }
    }
}