package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() != null ? item.getItemRequest().getId() : null
        );
    }

    public ItemOutDto toItemOutDto(Item item, BookingOutDto lastBooking, List<CommentOutDto> comments, BookingOutDto nextBooking) {
        return new ItemOutDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                comments,
                nextBooking,
                item.getItemRequest() != null ? item.getItemRequest().getId() : null);

    }

    public ItemOutDto toItemOutDto(Item item) {

        ItemOutDto itemDtoOut = new ItemOutDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
        if (item.getItemRequest() != null) {
            itemDtoOut.setRequestId(item.getItemRequest().getId());
        }
        return itemDtoOut;
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable());
    }
}