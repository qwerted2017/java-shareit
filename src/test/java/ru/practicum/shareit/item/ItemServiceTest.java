package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemService itemService;

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

    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("desc")
            .available(true)
            .owner(user)
            .build();

    private final ItemOutDto itemOutDto = ItemMapper.toItemOutDto(item);
    private final ItemDto itemDto = ItemMapper.toItemDto(item);

    private final Comment comment = Comment.builder()
            .commentId(1L)
            .text("comment")
            .created(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .status(BookingStatus.APPROVED)
            .item(item)
            .booker(user)
            .build();

    @Test
    void addNewItem() {
        lenient().when(userService.findById(user.getId())).thenReturn(userDto);
        lenient().when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemOutDto actualItemDto = itemService.add(userDto.getId(), ItemMapper.toItemDto(item));

        assertEquals(actualItemDto.getName(), "item");
        assertEquals(actualItemDto.getDescription(), "desc");
    }

    @Test
    void updateItem() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(), null);
        Item updatedItem = Item.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .available(false)
                .owner(user)
                .itemRequest(itemRequest)
                .build();

        when(userService.findById(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));
        lenient().when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemOutDto savedItem = itemService.update(user.getId(), itemOutDto.getId(), ItemMapper.toItemDto(updatedItem));

        assertEquals("updated name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
    }

    @Test
    void updateIncorrectItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.update(user.getId(), itemOutDto.getId(), ItemMapper.toItemDto(item)));
        assertEquals(itemNotFoundException.getMessage(), "Item not found");
    }

    @Test
    void getItemById() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemOutDto actualItemDto = itemService.findItemById(user.getId(), item.getId());

        assertEquals(itemOutDto, actualItemDto);
    }

    @Test
    void getItem44ById() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        lenient().when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.findItemById(user.getId(), 111L));

        assertEquals(notFoundException.getMessage(), "User with id " + user.getId() + " doesn't have item with id " + 111L);
    }

    @Test
    void createComment() {
        CommentOutDto expectedCommentDto = CommentMapper.toCommentOutDto(comment);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentOutDto actualCommentDto = itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId());

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void createCommentForUserWithoutBookings() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException userBookingsNotFoundException = assertThrows(ValidationException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(userBookingsNotFoundException.getMessage(), "User " + user.getId() + " haven't any bookings of item " + item.getId());

    }

    @Test
    void createCommentForUserdsfWithoutBookings() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        lenient().when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        NotFoundException commentNotFoundException = assertThrows(NotFoundException.class,
                () -> itemService.createComment(user.getId(), CommentMapper.toCommentDto(comment), 111L));

        assertEquals(commentNotFoundException.getMessage(), "User " + user.getId() + " haven't item " + 111L);

    }

    @Test
    void searchItems() {
        when(itemRepository.findAllByOwnerIdOrderById(anyLong())).thenReturn(List.of(item));

        List<ItemOutDto> actualItemsDto = itemService.findAll(1L);

        assertEquals(1, actualItemsDto.size());
        assertEquals(1, actualItemsDto.get(0).getId());
        assertEquals("item", actualItemsDto.get(0).getName());
    }


    @Test
    void searchValidText() {
        String text = "text";

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.search(text)).thenReturn(List.of(item));

        List<ItemDto> actualItemsDto = itemService.search(user.getId(), text);

        assertEquals(1, actualItemsDto.size());
        assertEquals(1, actualItemsDto.get(0).getId());
        assertEquals("item", actualItemsDto.get(0).getName());
    }

    @Test
    void searchEmptyText() {
        String text = "";

        when(userService.findById(user.getId())).thenReturn(userDto);

        List<ItemDto> actualItemsDto = itemService.search(user.getId(), text);

        assertEquals(0, actualItemsDto.size());
    }
}
