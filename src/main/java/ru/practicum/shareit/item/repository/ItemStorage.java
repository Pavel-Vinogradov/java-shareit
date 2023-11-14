package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getItems(long userId);

    Item getItem(long itemId);

    Item addItem(long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    boolean deleteItem(long itemId);

    boolean isItemExists(long id);

    List<Item> searchItems(String text);
}
