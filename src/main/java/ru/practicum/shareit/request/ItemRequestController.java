package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestOutDto addItemRequest(@RequestHeader(Constants.USER_HEADER) Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping()
    public List<ItemRequestOutDto> getUserItemRequests(@RequestHeader(Constants.USER_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);

    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAllItemsRequests(@RequestHeader(Constants.USER_HEADER) Long userId,
                                                       @RequestParam(required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {

        return itemRequestService.getAllRequests(userId, PageRequest.of(from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getItemRequest(@RequestHeader(Constants.USER_HEADER) Long userId,
                                            @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
