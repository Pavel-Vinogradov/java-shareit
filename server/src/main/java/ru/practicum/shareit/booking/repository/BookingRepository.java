package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findFirstBookingByItemIdAndEndIsBeforeAndStatusNotLikeOrderByEndDesc(Long itemId,
                                                                                 LocalDateTime dateTime,
                                                                                 Status status);

    Booking findFirstBookingByItemIdAndStartIsAfterAndStatusNotLikeOrderByStartAsc(Long itemId,
                                                                                   LocalDateTime dateTime,
                                                                                   Status status);

    Booking findFirstBookingByItemIdAndStartIsBeforeAndStatusNotLikeOrderByStartDesc(Long itemId,
                                                                                     LocalDateTime dateTime,
                                                                                     Status status);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime timeNow, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime timeNow, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(long userId, Status status);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStatus(long userId, Status status);

    List<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndStartIsAfter(long userId, LocalDateTime timeNow, Sort sort);

    List<Booking> findAllByItem_OwnerIdAndEndIsBefore(long userId, LocalDateTime timeNow, Sort sort);

}
