package ru.practicum.shareit.request;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestService requestService;

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

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("request description")
            .items(List.of(item))
            .build();

    @Test
    void addNewRequest() {
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
        ItemRequestOutDto expectedRequestDto = ItemRequestMapper.toItemRequestOutDto(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestOutDto actualRequestDto = requestService.add(user.getId(), requestDto);

        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void getUserRequests() {
        List<ItemRequestOutDto> expectedRequestsDto = List.of(ItemRequestMapper.toItemRequestOutDto(request));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findAllByRequestorId(userDto.getId())).thenReturn(List.of(request));

        List<ItemRequestOutDto> actualRequestsDto = requestService.getUserRequests(userDto.getId());

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getRequestById() {
        ItemRequestOutDto expectedRequestDto = ItemRequestMapper.toItemRequestOutDto(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        ItemRequestOutDto actualRequestDto = requestService.getRequestById(userDto.getId(), request.getId());

        assertEquals(expectedRequestDto, actualRequestDto);
    }


    @Test
    void getRequestByInvalidId() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        NotFoundException requestNotFoundException = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(userDto.getId(), request.getId()));

        assertEquals(requestNotFoundException.getMessage(), String.format("ItemRequest %s" +
                " not found.", request.getId()));
    }
}
