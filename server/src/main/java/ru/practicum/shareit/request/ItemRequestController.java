package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.utils.Constants.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestOutDto addItemRequest(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping()
    public List<ItemRequestOutDto> getUserItemRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);

    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAllItemsRequests(@RequestHeader(USER_HEADER) Long userId,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {

        return itemRequestService.getAllRequests(userId, PageRequest.of(from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getItemRequest(@RequestHeader(USER_HEADER) Long userId,
                                            @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
