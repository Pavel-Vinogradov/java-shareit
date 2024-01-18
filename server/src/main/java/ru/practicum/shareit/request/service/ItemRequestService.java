package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithRequest;

import java.util.List;

public interface ItemRequestService {
    RequestDto addItemRequest(RequestDto requestDto, long userId);

    List<RequestDtoWithRequest> getItemRequest(long userId);

    List<RequestDtoWithRequest> getAllItemRequest(long userId, int from, int size);

    RequestDtoWithRequest getRequestById(long userId, long requestId);
}
