package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with ID {} not found", userId);
            return new NotFoundException("User not found");
        });
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() -> {
            log.error("Category with ID{} not found", eventDto.getCategory());
            return new NotFoundException("Category not found");
        });
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("The date and time of the event cannot be earlier than two hours from the current moment.");
        }

        Location location = locationRepository.save(locationMapper.toLocation(eventDto.getLocation()));

        Event event = new Event();
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);
        event.setState(EventState.PENDING);
        eventRepository.save(eventMapper.toEvent(eventDto, category, userId));
        log.info("Event with ID {} created", event.getId());
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size) {
        userRepository.findById(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        return eventMapper.toEventFullDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        userRepository.findById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });
        if (event.getState() == EventState.CANCELED || !event.getRequestModeration()) {
            throw new ConflictException("You can only change cancelled events or events that are awaiting moderation.");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("The date and time of the event cannot be earlier than two hours from the current moment.");
        }

        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEvent.getCategory()).orElseThrow(() -> {
                        log.error("Category with ID {} not found", updateEvent.getCategory());
                        return new NotFoundException("Category not found");
                    }
            ));

        }

        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getEventDate() != null) {
            event.setEventDate(updateEvent.getEventDate());
        }

        if (updateEvent.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(updateEvent.getLocation()));
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
        }

        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getEventsByRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with ID {} not found", userId);
            return new NotFoundException("User not found");
        });

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });

        List<Request> requests = requestRepository.findByRequesterIdAndEventId(userId, eventId);

        return requestMapper.toParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult confirmEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with ID {} not found", userId);
            return new NotFoundException("User not found");
        });

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("The request cannot be confirmed if the limit on requests for this event has already been reached.");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            List<Request> requests = requestRepository.findById(requestDto.getRequestIds());
            for (Request request : requests) {
                confirmRequest(request, event);
            }
            List<ParticipationRequestDto> requestDtos = requestMapper.toParticipationRequestDto(requests);
            return new EventRequestStatusUpdateResult(
                    requestDtos,
                    null
            );
        }

        return null;////////////////////////////////////////

    }

    @Override
    @Transactional
    public List<EventFullDto> getEvents(List<Long> userId, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("The beginning of the range cannot be later than its end");
        }

        List<Event> events = eventRepository.findEventsByFilters(userId, states, categories,
                rangeStart, rangeEnd,PageRequest.of(from / size, size));

        return eventMapper.toEventFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });

        if (event.getState() != EventState.PENDING && event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Administrator can only update events in the PENDING or PUBLISHED state.");
        }

        if(eventDto.getEventDate() != null) {
            LocalDateTime minAllowedDate = event.getPublishedOn().plusHours(1);
            if (eventDto.getEventDate().isBefore(minAllowedDate)) {
                throw new ConflictException("The date of the event must be no earlier than one hour after publication.");
            }
            event.setEventDate(eventDto.getEventDate());
        }

        if(eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if(eventDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory()).orElseThrow(() -> {
                log.error("Category with ID {} not found", eventDto.getCategory());
                return new NotFoundException("Category not found");
            }));
        }

        if(eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if(eventDto.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(eventDto.getLocation()));
        }

        if(eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if(eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if(eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if(eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

        if(eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (!event.getState().equals(EventState.PENDING)) {
                        throw new ConflictException("The event must be in the PENDING state for publication");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;

                case REJECT_EVENT:
                    if (!event.getState().equals(EventState.PENDING)) {
                        throw new ConflictException("The event must be in the PENDING state for publication");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        return eventMapper.toEventFullDto(event);
    }

    public EventShortDto getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                         Integer size) {

        return null;////////////////////////////////////////
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventPublicById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> {
            log.error("Event with ID {} not found", id);
            return new NotFoundException("Event not found");
        });

        if(event.getState() != EventState.PUBLISHED) {
            log.error("Event with ID {} not found", id);
            throw new NotFoundException("Event not found");
        }
        return null;////////////////////////////////////////
    }


    private void confirmRequest(Request request, Event event) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ConflictException("The status can only be changed for applications that are in a waiting state");
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            request.setStatus(RequestStatus.REJECTED);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
    }
}
