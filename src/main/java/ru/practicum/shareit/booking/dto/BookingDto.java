package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.enums.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Validated
@Builder
public class BookingDto {
    private Long id;
    @NotNull(message = "Дата начала бронирования не может быть пустой")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;
    private long bookerId;
    private Long itemId;
    private BookingDtoItem item;
    private BookingDtoUser booker;
    private Status status;
}