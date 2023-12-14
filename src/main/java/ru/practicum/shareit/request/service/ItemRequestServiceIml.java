package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithRequest;
import ru.practicum.shareit.request.mappers.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mappers.RequestMapper.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceIml implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public RequestDto addItemRequest(RequestDto requestDto, long userId) {
        Optional<User> newUser = userRepository.findById(userId);
        if (newUser.isPresent()) {
            User user = newUser.get();

            Request request = toItemRequest(user, requestDto);
            request.setRequestor(user);
            request.setCreated(LocalDateTime.now());

            return toItemRequestDto(itemRequestRepository.save(request));
        } else {
            throw new UserNotFoundException("Пользователь с id " + "userId" + "не найден");
        }
    }

    @Override
    public List<RequestDtoWithRequest> getItemRequest(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден " + userId));
        List<RequestDtoWithRequest> requestDtoWithRequests =
                itemRequestRepository.findAllByRequestorId(userId).stream()
                        .map(RequestMapper::toRequestDtoWithRequest)
                        .collect(Collectors.toList());
        for (RequestDtoWithRequest withRequest : requestDtoWithRequests) {
            for (ItemDtoReq item : withRequest.getItems()) {
                item.setRequestId(withRequest.getId());
            }
        }
        return requestDtoWithRequests;
    }

    @Override
    public List<RequestDtoWithRequest> getAllItemRequest(long userId, int from, int size) {
        if ((from < 0 || size < 0 || (from == 0 && size == 0))) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "неверный параметр пагинации");
        }

        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден " + userId));
        List<Request> byOwnerId = itemRequestRepository.findAllByRequestorIdIsNot(userId, PageRequest.of(from / size, size));
        List<RequestDtoWithRequest> requestDtoWithRequests =
                byOwnerId.stream()
                        .map(RequestMapper::toRequestDtoWithRequest)
                        .collect(Collectors.toList());
        for (RequestDtoWithRequest withRequest : requestDtoWithRequests) {
            for (ItemDtoReq item : withRequest.getItems()) {
                item.setRequestId(withRequest.getId());
            }
        }
        return requestDtoWithRequests;
    }

    @Override
    public RequestDtoWithRequest getRequestById(long userId, long requestId) throws RequestNotFoundException {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден " + userId));
        Request request = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new RequestNotFoundException("Запрос предмета по id не найден"));
        RequestDtoWithRequest requestDtoWithRequest = toRequestDtoWithRequest(request);
        for (ItemDtoReq item : requestDtoWithRequest.getItems()) {
            item.setRequestId(requestId);
        }
        return requestDtoWithRequest;
    }
}
