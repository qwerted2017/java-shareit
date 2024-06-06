package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private ItemRepository itemRepository;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@email.ru")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user")
            .email("user@email.ru")
            .build();

    private final User owner = User.builder()
            .id(2L)
            .name("owner")
            .email("owner@email.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("desc")
            .available(true)
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.APPROVED)
            .item(item)
            .booker(user)
            .build();

    private final Booking bookingWaiting = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.WAITING)
            .item(item)
            .booker(user)
            .build();

    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    @Test
    void createValidBooking() {
        BookingOutDto expectedBookingOutDto = BookingMapper.toBookingOut(BookingMapper.toBooking(user, item, bookingDto));
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.toBooking(user, item, bookingDto));

        BookingOutDto actualBookingOutDto = bookingService.add(userDto.getId(), bookingDto);

        assertEquals(expectedBookingOutDto, actualBookingOutDto);
    }

    @Test
    void createWhenItemIsNotAvailable() {
        item.setAvailable(false);
        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class,
                () -> bookingService.add(userDto.getId(), bookingDto));

        assertEquals(bookingValidationException.getMessage(), "Item not available");
    }

    @Test
    void findForeignItem() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException bookingNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findBookingByUserId(3L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "User is not booker or owner");
    }

    @Test
    void update() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingOutDto actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(BookingStatus.APPROVED, actualBookingDtoOut.getStatus());
    }


    @Test
    void updateForeignItemInWaiting() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.update(owner.getId(), booking.getId(), false));

        assertEquals(validationException.getMessage(), "Booking is in WAITING status");
    }

    @Test
    void findWrongBookingId() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException bookingNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findBookingByUserId(1L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), "Booking not found.");
    }

    @Test
    void getAllByBookerWhenBookingStatePAST() {
        List<BookingOutDto> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllPastBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.findAll(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwnerWhenBookingStateAll() {
        List<BookingOutDto> expectedBookingsDtoOut = List.of(BookingMapper.toBookingOut(booking));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(bookingRepository.findAllBookingsByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.findAllOwner(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void addBookingWrongStartTime() {

        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));

        when(userService.findById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.add(user.getId(), bookingDto));
    }
}
