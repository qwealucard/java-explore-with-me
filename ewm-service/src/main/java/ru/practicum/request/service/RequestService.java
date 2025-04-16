package ru.practicum.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Transactional
public interface RequestService {

    List<ParticipationRequestDto> getRequests(Long id);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
