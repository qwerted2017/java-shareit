package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
    @Query(value = "SELECT b.* from bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllBookingsByBookerId(Long bookerId, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND ?2 BETWEEN b.start_time AND b.end_time " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllCurrentBookingsByBookerId(Long bookerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.end_time < ?2 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllPastBookingsByBookerId(Long bookerId, LocalDateTime currentTime, Pageable pageable);


    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.start_time > ?2 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllFutureBookingsByBookerId(Long bookerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.status = 'WAITING' " +
            "AND b.start_time > ?2 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllWaitingBookingsByBookerId(Long bookerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllRejectedBookingsByBookerId(Long bookerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id  " +
            "WHERE i.owner_id = ?1 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllBookingsByOwnerId(Long ownerId, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND ?2 BETWEEN b.start_time AND b.end_time " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllCurrentBookingsByOwnerId(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.end_time < ?2 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllPastBookingsByOwnerId(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.start_time > ?2 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllFutureBookingsByOwnerId(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.status = 'WAITING' " +
            "AND b.start_time > ?2 " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllWaitingBookingsByOwnerId(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start_time DESC", nativeQuery = true)
    Page<Booking> findAllRejectedBookingsByOwnerId(Long ownerId, Pageable pageable);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_time < ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_time DESC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getLastBooking(Long idItem, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_time > ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_time ASC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getNextBooking(Long idItem, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND i.id = ?2 " +
            "AND b.status = 'APPROVED' " +
            "AND b.end_time < ?3 ", nativeQuery = true)
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, BookingStatus status);

    List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, BookingStatus bookingStatus);
}
