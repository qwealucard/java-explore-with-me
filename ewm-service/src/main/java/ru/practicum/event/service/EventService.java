package ru.practicum.event.service;

import org.springframework.cglib.core.Local;
import ru.practicum.event.dto.*;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size);
    EventFullDto getEventById(Long eventId);
    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    List<ParticipationRequestDto> getEventsByRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult confirmEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEvents(List<Long> userId, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long id, UpdateEventAdminRequest eventDto);

    EventShortDto getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                  Integer size);

    EventFullDto getEventPublicById(Long id);
}
