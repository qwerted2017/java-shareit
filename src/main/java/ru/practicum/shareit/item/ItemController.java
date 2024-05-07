package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader(USER_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(USER_HEADER) Long userId, @PathVariable("itemId") Long itemId) {
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(USER_HEADER) Long userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_HEADER) Long userId, @RequestParam(name = "text") String text) {
        return itemService.search(userId, text);
    }

}