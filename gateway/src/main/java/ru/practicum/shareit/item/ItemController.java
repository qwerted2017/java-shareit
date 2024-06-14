package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.utils.Constants.USER_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("POST create new item: {} from user id: {}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable("itemId") Long itemId) {
        log.info("PATCH update item id: {} from user id: {}", itemId, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable Long itemId) {
        log.info("GET item with id: {}", itemId);
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET all items of user id: {}", userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam(name = "text") String text,
                                              @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET all items with text: {}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_HEADER) Long userId,
                                                @Validated @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId) {
        log.info("GET new comment with text: {} to item id: {} from user id : {}", commentDto.getText(), itemId, userId);
        return itemClient.createComment(userId, commentDto, itemId);
    }
}