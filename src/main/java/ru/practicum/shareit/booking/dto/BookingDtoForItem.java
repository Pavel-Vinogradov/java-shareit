package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDtoForItem {
    @NotNull(message = "Не указана вещь")
    private long itemId;
    @NotNull(message = "Дата начала бронирования не может быть пустой")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;
}