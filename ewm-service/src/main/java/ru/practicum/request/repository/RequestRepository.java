package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long id);
    boolean existsByEventIdAndRequesterId(Long UserId, Long eventId);
    List<Request> findByRequesterIdAndEventId(Long userId, Long eventId);
    List<Request> findById(List<Long> id);
}
