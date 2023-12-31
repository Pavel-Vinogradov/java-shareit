package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class RequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public RequestDto addItemRequest(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                     @Valid @RequestBody(required = false) RequestDto requestDto) {
        log.info("Получен POST-запрос /requests {} ", requestDto);
        return itemRequestService.addItemRequest(requestDto, userId);
    }

    @GetMapping
    public List<RequestDtoWithRequest> getRequests(@RequestHeader(name = USER_ID_HEADER) Long userId) {
        log.info("Получен GET-запрос на получение списка своих запросов вместе с данными о них.");
        return itemRequestService.getItemRequest(userId);
    }

    @GetMapping(path = "/all")
    public List<RequestDtoWithRequest> getAllRequests(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                      @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(required = false, defaultValue = "20") @Positive int size
    ) {
        log.info("Получен GET-запрос на получение списка запросов, созданных другими пользователями. " +
                "Результаты возвращаются постранично от {} в количестве {}.", from, size);
        return itemRequestService.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDtoWithRequest getRequestById(@RequestHeader(name = USER_ID_HEADER) Long userId,
                                                @PathVariable Long requestId) {
        log.info("Получен GET-запрос на получение данных об одном конкретном запросе с данными об ответах.");
        return itemRequestService.getRequestById(userId, requestId);
    }
}

