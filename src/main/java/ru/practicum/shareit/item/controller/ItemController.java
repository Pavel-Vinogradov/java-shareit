package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@RequestBody @Valid ItemDto itemDto,
                            @RequestHeader(name = USER_ID_HEADER) long userId) {
        log.info("Получен POST-запрос /items {} ", itemDto);
        return itemService.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = USER_ID_HEADER) Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Получен PATCH-запрос /itemId {} ", itemId);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Получен GET-запрос /itemId {} ", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader(name = USER_ID_HEADER) long userId) {
        log.info("Получен GET-запрос: список всех предметов одного пользователя {} ", userId);
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam @NotBlank String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        log.info("Получен GET-запрос /text {} ", text);
        return itemService.searchItem(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(name = USER_ID_HEADER) long userId,
                           @PathVariable long itemId) {
        log.info("Получен DELETE- запрос на удаление вещи {} ", itemId);
        itemService.deleteItemById(userId, itemId);
    }

}
