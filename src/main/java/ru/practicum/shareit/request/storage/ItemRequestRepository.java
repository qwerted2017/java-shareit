package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Transactional
@Repository
public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long userId);

    Page<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long userId, Pageable pageable);
}
