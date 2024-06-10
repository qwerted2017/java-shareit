package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {

        return ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .build();
    }

    public ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest) {
        List<ItemOutDto> itemsOutDto = new ArrayList<>();

        if (!Objects.isNull(itemRequest.getItems())) {
            itemsOutDto = itemRequest.getItems().stream()
                    .map(ItemMapper::toItemOutDto)
                    .collect(Collectors.toList());
        }
        return ItemRequestOutDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsOutDto)
                .build();
    }
}