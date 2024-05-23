package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(User user, Item item, BookingDto bookingDto) {
        return new Booking(
                item,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                user,
                BookingStatus.WAITING);
    }

    public BookingOutDto toBookingOut(Booking booking) {
        return new BookingOutDto(
                booking.getId(),
                ItemMapper.toItemOutDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId());
    }
}
