package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterIdOrderByIdAsc(Long userId);

    List<Request> findAllByRequesterIdIsNot(Long userId, PageRequest pageRequest);

}
