package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getEventById(Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult confirmEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEvents(List<Long> userId, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long id, UpdateEventAdminRequest eventDto);

    List<EventShortDto> getEventsPublic(EventPublicFilter filter);

    EventFullDto getEventPublicById(Long id, HttpServletRequest httpServletRequest);
}
