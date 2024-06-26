package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(Long userId);

    Page<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long userId, Pageable pageable);
}
