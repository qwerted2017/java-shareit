package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Constants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemOutDto add(@RequestHeader(Constants.USER_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemOutDto update(@RequestHeader(Constants.USER_HEADER) Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemOutDto findById(@RequestHeader(Constants.USER_HEADER) Long userId, @PathVariable("itemId") Long itemId) {
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemOutDto> findAll(@RequestHeader(Constants.USER_HEADER) Long userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(Constants.USER_HEADER) Long userId, @RequestParam(name = "text") String text) {
        return itemService.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDto createComment(@RequestHeader(Constants.USER_HEADER) Long userId,
                                       @Validated @RequestBody CommentDto commentDto,
                                       @PathVariable Long itemId) {
        return itemService.createComment(userId, commentDto, itemId);
    }
}