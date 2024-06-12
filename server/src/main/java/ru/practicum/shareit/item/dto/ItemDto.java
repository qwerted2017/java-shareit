package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ItemDto {
    private Long id;

    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}