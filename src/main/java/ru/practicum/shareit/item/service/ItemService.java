package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemOutDto add(Long userId, ItemDto itemDto) {
        UserDto userDto = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestRepository.findById(itemDto.getRequestId()).orElse(null));
        }
        return ItemMapper.toItemOutDto(itemRepository.save(item));
    }

    @Transactional
    public ItemOutDto update(Long userId, Long itemId, ItemDto itemDto) {
        UserDto userDto = userService.findById(userId);
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            if (!item.get().getOwner().equals(UserMapper.toUser(userDto))) {
                throw new NotFoundException("User " + userId + " is not owner to item with id " + itemId);
            }

            Item savedItem = item.get();

            Item newItem = ItemMapper.toItem(itemDto);

            if (Objects.isNull(newItem.getName())) {
                newItem.setName(savedItem.getName());
            }
            if (Objects.isNull(newItem.getDescription())) {
                newItem.setDescription(savedItem.getDescription());
            }
            if (Objects.isNull(newItem.getAvailable())) {
                newItem.setAvailable(savedItem.getAvailable());
            }
            newItem.setId(savedItem.getId());
            newItem.setItemRequest(savedItem.getItemRequest());
            newItem.setOwner(savedItem.getOwner());

            return ItemMapper.toItemOutDto(itemRepository.save(newItem));
        } else {
            throw new NotFoundException("Item not found");
        }
    }

    @Transactional
    public ItemOutDto findItemById(Long userId, Long itemId) {
        UserDto userDto = userService.findById(userId);
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException(String.format("User with id %s doesn't have item with id %s", userId, itemId));
        }
        ItemOutDto itemOutDto = ItemMapper.toItemOutDto(item.get());
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        List<CommentOutDto> commentOutDtoRespons = comments.stream()
                .map(CommentMapper::toCommentOutDto)
                .collect(toList());
        itemOutDto.setComments(commentOutDtoRespons);

        if (!item.get().getOwner().getId().equals(userId)) {
            return itemOutDto;
        }
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartAsc(item.get(), BookingStatus.APPROVED);
        List<BookingOutDto> bookingOutDto = bookings.stream()
                .map(BookingMapper::toBookingOut)
                .collect(toList());

        itemOutDto.setLastBooking(getLastBooking(bookingOutDto, LocalDateTime.now()));
        itemOutDto.setNextBooking(getNextBooking(bookingOutDto, LocalDateTime.now()));

        return itemOutDto;
    }

    public List<ItemOutDto> findAll(Long userId) {
        UserDto userDto = userService.findById(userId);
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderById(userId);
        List<Long> itemsId = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<CommentOutDto>> comments = commentRepository.findAllByItemIdIn(itemsId)
                .stream()
                .map(CommentMapper::toCommentOutDto)
                .collect(groupingBy(CommentOutDto::getItemId, toList()));

        Map<Long, List<BookingOutDto>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(itemList, BookingStatus.APPROVED)
                .stream()
                .map(BookingMapper::toBookingOut)
                .collect(groupingBy(BookingOutDto::getItemId, toList()));

        return itemList.stream()
                .map(item -> ItemMapper.toItemOutDto(item,
                        getLastBooking(bookings.get(item.getId()), LocalDateTime.now()),
                        comments.get(item.getId()),
                        getNextBooking(bookings.get(item.getId()), LocalDateTime.now())
                ))
                .collect(toList());
    }

    public List<ItemDto> search(Long userId, String searchText) {
        UserDto userDto = userService.findById(userId);
        if (searchText.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.search(searchText);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentOutDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = UserMapper.toUser(userService.findById(userId));
        Optional<Item> itemById = itemRepository.findById(itemId);

        if (itemById.isEmpty()) {
            throw new NotFoundException("User " + userId + " haven't item " + itemId);
        }
        Item item = itemById.get();

        List<Booking> userBookings = bookingRepository.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException("User " + userId + " haven't any bookings of item " + itemId);
        }

        return CommentMapper.toCommentOutDto(commentRepository.save(CommentMapper.toComment(commentDto, item, user)));
    }

    private BookingOutDto getLastBooking(List<BookingOutDto> bookings, LocalDateTime time) {

        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> !bookingDTO.getStart().isAfter(time))
                .reduce((booking1, booking2) -> booking1.getStart().isAfter(booking2.getStart()) ? booking1 : booking2)
                .orElse(null);
    }

    private BookingOutDto getNextBooking(List<BookingOutDto> bookings, LocalDateTime time) {

        if (bookings == null || bookings.isEmpty()) {
            return null;
        }

        return bookings
                .stream()
                .filter(bookingDTO -> bookingDTO.getStart().isAfter(time))
                .findFirst()
                .orElse(null);
    }
}