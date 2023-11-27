package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Data
public class ItemDto {

    private Long id;
    @NotBlank(message = "Name не должен быть пустым")
    private String name;
    @NotBlank
    @NotBlank(message = "description не должен быть пустым")
    private String description;
    private Long ownerId;
    @NotNull(message = "available не должен отсутствовать")
    private Boolean available;
    private List<CommentDto> comments;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
}
