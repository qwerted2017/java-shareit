package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
//    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemDto add(Long userId, ItemDto itemDto) {
        UserDto userDto = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
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

            return ItemMapper.toItemDto(itemRepository.save(newItem));
        }
        return itemDto;
    }

    public ItemDto findItemById(Long userId, Long itemId) {
        UserDto userDto = userService.findById(userId);
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException(String.format("User with id %s doesn't have item with id %s", userId, itemId));
        }
        return ItemMapper.toItemDto(item.get());
    }

    public List<ItemDto> findAll(Long userId) {
        UserDto userDto = userService.findById(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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

}