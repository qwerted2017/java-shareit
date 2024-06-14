package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestClient;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.utils.Constants.USER_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_HEADER) Long userId,
                                      @Valid @RequestBody ItemRequestDto requestDto) {
        return itemRequestClient.addNewRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}