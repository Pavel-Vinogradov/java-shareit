package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoReq;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest())
                .build();
    }

   public static Item toItem(User user, ItemDto itemDto) {
          return Item.builder()
                  .id(itemDto.getId())
                  .name(itemDto.getName())
                  .description(itemDto.getDescription())
                  .ownerId(user.getId())
                  .available(itemDto.getAvailable())
                  .build();
    }

    public static ItemDtoReq toItemDtoReq(Item item) {
        return ItemDtoReq.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequest())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemDtoReq> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDtoReq)
                .collect(Collectors.toList());
    }
}
