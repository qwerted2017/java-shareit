package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(User.builder().id(1L).name("name").email("user@email.com").build())
            .item(new Item())
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusMinutes(10))
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void toBookingItemDto() {
        BookingOutDto actualBookingItemDto = BookingMapper.toBookingOut(booking);

        assertEquals(1L, actualBookingItemDto.getId());
        assertEquals(1L, actualBookingItemDto.getBookerId());
    }
}
