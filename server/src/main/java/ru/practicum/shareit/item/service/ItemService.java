package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getItemsByUser(long userId, int from, int size);

    Collection<ItemDto> searchItem(String text, int from, int size);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

    void deleteItemById(long itemId);
}
