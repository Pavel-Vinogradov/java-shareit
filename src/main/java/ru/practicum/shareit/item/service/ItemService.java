package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    public List<Item> getItems(Long userId) {
        return itemStorage.getItems(userId);
    }

    public Item getItem(Long itemId) {
        if (itemStorage.isItemExists(itemId)) {
            throw new NotFoundException("Item не найден.");
        }
        return itemStorage.getItem(itemId);
    }

    public Item addItem(Long userId, Item item) {
        if (userStorage.isUserExistsById(userId)) {
            throw new NotFoundException("User пользователь не найден.");
        }
        if (ItemValidator.itemCheck(item)) {
            throw new InvalidEntityException("Не верно значение.");
        }
        return itemStorage.addItem(userId, item);
    }

    public Item updateItem(Long userId, Long itemId, Item item) {
        if (itemStorage.isItemExists(itemId)) {
            throw new NotFoundException("Item не найден.");
        }
        return itemStorage.updateItem(userId, itemId, item);
    }

    public Boolean deleteItem(Long itemId) {
        if (itemStorage.isItemExists(itemId)) {
            throw new NotFoundException("Item not found.");
        }
        return itemStorage.deleteItem(itemId);
    }

    public List<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
