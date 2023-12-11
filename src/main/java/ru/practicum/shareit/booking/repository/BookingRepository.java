package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.Collection;

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

    Collection<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    Collection<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime timeNow, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime timeNow, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatus(long userId, Status status);

    Collection<Booking> findAllByItem_OwnerIdOrderByStartDesc(long userId);

    Collection<Booking> findAllByItem_OwnerIdAndStatus(long userId, Status status);

    Collection<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime dateTime, LocalDateTime date, Sort sort);

    Collection<Booking> findAllByItem_OwnerIdAndStartIsAfter(long userId, LocalDateTime timeNow,Sort sort);

    Collection<Booking> findAllByItem_OwnerIdAndEndIsBefore(long userId, LocalDateTime timeNow,Sort sort);

}
