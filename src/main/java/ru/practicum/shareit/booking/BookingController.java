package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.Constants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto create(@RequestHeader(Constants.USER_HEADER) Long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Request for new booking for userId: {}", userId);
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto updateStatus(@RequestHeader(Constants.USER_HEADER) Long userId,
                                      @PathVariable("bookingId")
                                      Long bookingId,
                                      @RequestParam(name = "approved") Boolean approved) {
        log.info("Update item's booking status of userId: {}", userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto findBookingById(@RequestHeader(Constants.USER_HEADER) Long userId,
                                         @PathVariable("bookingId")
                                         Long bookingId) {
        log.info("Get booking status form userId: {}", userId);
        return bookingService.findBookingByUserId(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> findAllForUser(@RequestHeader(Constants.USER_HEADER) Long userId,
                                              @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("Get all booking for userId: {} and status: {}", userId, bookingState);
        return bookingService.findAll(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllOwnerBookings(@RequestHeader(Constants.USER_HEADER) Long ownerId,
                                                   @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("Get all bookings of ownerId: {} and status: {}", ownerId, bookingState);
        return bookingService.findAllOwner(ownerId, bookingState);
    }
}