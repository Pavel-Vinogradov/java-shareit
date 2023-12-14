package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
    public ItemDto updateItem(@RequestHeader(name = USER_ID_HEADER) long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Получен PATCH-запрос /itemId {} ", itemId);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(name = USER_ID_HEADER) long userId,
                               @PathVariable Long itemId) {
        log.info("Получен GET-запрос /itemId {} ", itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader(name = USER_ID_HEADER) long userId,
                                        @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                        @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("Получен GET-запрос: список всех предметов одного пользователя {} ", userId);
        return itemService.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam @NotBlank String text,
                                          @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                          @RequestParam(required = false, defaultValue = "10") @Min(0) int size
    ) {
        log.info("Получен GET-запрос /text {} ", text);
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(name = USER_ID_HEADER) long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody CommentDto commentDto) {
        log.info("Получен POST-запрос: добавление отзыва о бронировании предмета {} ", itemId);
        return itemService.postComment(userId, itemId, commentDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(name = USER_ID_HEADER)
                           @PathVariable long itemId) {
        log.info("Получен DELETE- запрос на удаление предмета {} ", itemId);
        itemService.deleteItemById(itemId);
    }
}