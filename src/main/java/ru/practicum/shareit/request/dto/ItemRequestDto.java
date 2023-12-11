package ru.practicum.shareit.request.dto;

import lombok.*;


import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private long requestorId;
}