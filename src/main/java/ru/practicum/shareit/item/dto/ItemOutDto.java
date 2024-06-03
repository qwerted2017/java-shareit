package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemOutDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingOutDto lastBooking;
    private List<CommentOutDto> comments;
    private BookingOutDto nextBooking;
    private Long requestId;

    public ItemOutDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemOutDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}