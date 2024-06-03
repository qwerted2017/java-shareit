package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public ItemRequestOutDto add(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, itemRequestDto);
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestOutDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestOutDto> getUserRequests(Long userId) {
        User user = UserMapper.toUser(userService.findById(userId));
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(userId);
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestOutDto)
                .collect(Collectors.toList());
    }

    public List<ItemRequestOutDto> getAllRequests(Long userId, Pageable pageable) {
        User user = UserMapper.toUser(userService.findById(userId));
        Page<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable);
        return itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestOutDto)
                .collect(Collectors.toList());
    }

    public ItemRequestOutDto getRequestById(Long userId, Long requestId) {
        User user = UserMapper.toUser(userService.findById(userId));
        Optional<ItemRequest> requestById = itemRequestRepository.findById(requestId);
        if (requestById.isEmpty()) {
            throw new NotFoundException(String.format("ItemRequest %s not found.", requestId));
        }
        return ItemRequestMapper.toItemRequestOutDto(requestById.get());
    }
}
