package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.ItemOutDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingOutDto {

    private Long id;
    private ItemOutDto item;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserDto booker;
    private BookingStatus status;

    public Long getItemId() {
        return item.getId();
    }

    public long getBookerId() {
        return booker.getId();
    }
}

