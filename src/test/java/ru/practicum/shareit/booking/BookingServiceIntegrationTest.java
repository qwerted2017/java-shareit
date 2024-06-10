package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    private final UserDto userDto1 = UserDto.builder()
            .name("user")
            .email("user@email.ru")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("user")
            .email("user2@email.ru")
            .build();

    private final ItemDto itemDto1 = ItemDto.builder()
            .name("item1")
            .description("desc1")
            .available(true)
            .build();

    private final ItemDto itemDto2 = ItemDto.builder()
            .name("item2")
            .description("desc2")
            .available(true)
            .build();

    private final BookingDto bookingDto1 = BookingDto.builder()
            .itemId(2L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    @Test
    void addBooking() {
        UserDto user1 = userService.add(userDto1);
        UserDto user2 = userService.add(userDto2);
        itemService.add(user1.getId(), itemDto1);
        itemService.add(user2.getId(), itemDto2);

        BookingOutDto bookingOutDto1 = bookingService.add(user1.getId(), bookingDto1);
        BookingOutDto bookingOutDto2 = bookingService.add(user1.getId(), bookingDto1);

        assertEquals(1L, bookingOutDto1.getId());
        assertEquals(2L, bookingOutDto2.getId());
        assertEquals(BookingStatus.WAITING, bookingOutDto1.getStatus());
        assertEquals(BookingStatus.WAITING, bookingOutDto2.getStatus());

        BookingOutDto update1 = bookingService.update(user2.getId(),
                bookingOutDto1.getId(), true);
        BookingOutDto update2 = bookingService.update(user2.getId(),
                bookingOutDto2.getId(), true);

        assertEquals(BookingStatus.APPROVED, update1.getStatus());
        assertEquals(BookingStatus.APPROVED, update2.getStatus());

        List<BookingOutDto> bookingsDtoOut = bookingService.findAllOwner(user2.getId(),
                BookingState.ALL.toString(), 0, 10);

        assertEquals(2, bookingsDtoOut.size());
    }
}
