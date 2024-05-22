package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingOutDto add(Long userId, BookingDto bookingDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        Optional<Item> itemById = itemRepository.findById(bookingDto.getItemId());
        if (itemById.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        Item item = itemById.get();
        if (!item.getAvailable()) {
            throw new ValidationException("Item not available");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Item not found");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Item start time is not valid");
        }

        Booking booking = BookingMapper.toBooking(user, item, bookingDto);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    @Transactional
    public BookingOutDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = checkBooking(userId, bookingId, 1);
        if (booking == null) {
            throw new NotFoundException("Booking not found");
        }
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        return BookingMapper.toBookingOut(bookingRepository.save(booking));
    }

    public BookingOutDto findBookingByUserId(Long userId, Long bookingId) {
        Booking booking = checkBooking(userId, bookingId, 2);
        assert booking != null;
        return BookingMapper.toBookingOut(booking);
    }

    public List<BookingOutDto> findAll(Long bookerId, String state) {
        userService.findById(bookerId);
        switch (checkState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByBookerId(bookerId).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(bookerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<BookingOutDto> findAllOwner(Long ownerId, String state) {
        userService.findById(ownerId);
        switch (checkState(state)) {
            case ALL:
                return bookingRepository.findAllBookingsByOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }


    private Booking checkBooking(Long userId, Long bookingId, Integer number) {
        Optional<Booking> bookingById = bookingRepository.findById(bookingId);
        if (bookingById.isEmpty()) {
            throw new NotFoundException("Booking not found.");
        }
        Booking booking = bookingById.get();
        switch (number) {
            case 1:
                if (!booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("User is not booker");
                }
                if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                    throw new ValidationException("Booking is in WAITING status");
                }
                return booking;
            case 2:
                if (!booking.getBooker().getId().equals(userId)
                        && !booking.getItem().getOwner().getId().equals(userId)) {
                    throw new NotFoundException("User is not booker or owner ");
                }
                return booking;
        }
        return null;
    }

    private BookingState checkState(String bookingState) {
        BookingState state = BookingState.from(bookingState);
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + bookingState);
        }
        return state;
    }
}
