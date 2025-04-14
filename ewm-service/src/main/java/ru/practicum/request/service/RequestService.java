package ru.practicum.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    @Transactional(readOnly = true)
    List<ParticipationRequestDto> getRequests(Long id);

    @Transactional
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    @Transactional
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
