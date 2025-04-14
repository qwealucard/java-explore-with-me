package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    @Transactional
    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    @Transactional(readOnly = true)
    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);


    @Transactional(readOnly = true)
    EventFullDto getEventById(Long eventId);

    @Transactional
    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    @Transactional(readOnly = true)
    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    @Transactional
    EventRequestStatusUpdateResult confirmEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    @Transactional(readOnly = true)
    List<EventFullDto> getEvents(List<Long> userId, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Integer from, Integer size);

    @Transactional
    EventFullDto updateEventByAdmin(Long id, UpdateEventAdminRequest eventDto);

    @Transactional
    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                        Integer size, HttpServletRequest httpServletRequest);

    @Transactional(readOnly = true)
    EventFullDto getEventPublicById(Long id, HttpServletRequest httpServletRequest);
}
