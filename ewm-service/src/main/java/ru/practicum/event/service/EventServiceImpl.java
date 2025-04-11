package ru.practicum.event.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitRequest;

import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.*;
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
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with ID {} not found", userId);
            return new NotFoundException("User not found");
        });
        UserShortDto userShortDto = userMapper.toUserShortDto(user);
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() -> {
            log.error("Category with ID{} not found", eventDto.getCategory());
            return new NotFoundException("Category not found");
        });
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("The date and time of the event cannot be earlier than two hours from the current moment.");
        }
        Location location = locationMapper.toLocation(eventDto.getLocation());
        locationRepository.save(location);
        Event event = eventMapper.toEvent(eventDto, category, userId);
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(location);
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0L);
        eventRepository.save(event);
        log.info("Event with ID {} created", event.getId());
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        userRepository.findById(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        return eventMapper.toEventShortDto(events);
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

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("You are not the creator of the event");
        }

        if (event.getState() == EventState.PUBLISHED) {
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

        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == StateAction.SEND_TO_REVIEW) {
                if (event.getState() == EventState.CANCELED) {
                    event.setState(EventState.PENDING);
                } else {
                    throw new ConflictException("Событие уже находится в состоянии ожидания модерации");
                }
            } else if (updateEvent.getStateAction() == StateAction.CANCEL_REVIEW) {
                if (event.getState() == EventState.PENDING) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw new ConflictException("Событие уже находится в состоянии отмены");
                }
            }
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        userRepository.findById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("List of requests is available only to the initiator of the event");
        }
        return requestRepository.findByEventId(eventId).stream()
                                .map(requestMapper::toParticipationRequestDto)
                                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult confirmEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateDto) {
        userRepository.findById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Only the initiator of the event can update requests.");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("The event has not been published yet");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Moderation of the request to participate in this event is not required.");
        }

        List<Request> requests = requestRepository.findAllById(updateDto.getRequestIds());
        if (requests.size() != updateDto.getRequestIds().size()) {
            throw new NotFoundException("Some requests were not found");
        }

        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("You cannot change the status of an request that is not in the waiting state.");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        Long currentConfirmed = event.getConfirmedRequests();
        Long participantLimit = event.getParticipantLimit();

        if (RequestStatus.CONFIRMED.equals(updateDto.getStatus()) && currentConfirmed >= participantLimit) {
            throw new ConflictException("The limit of participants has been reached");
        }

        for (Request req : requests) {
            if (RequestStatus.CONFIRMED.equals(updateDto.getStatus())) {
                if (currentConfirmed < participantLimit) {
                    req.setStatus(RequestStatus.CONFIRMED);
                    currentConfirmed++;
                    confirmedRequests.add(requestMapper.toParticipationRequestDto(req));
                } else {
                    req.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.toParticipationRequestDto(req));
                }
            } else if (RequestStatus.REJECTED.equals(updateDto.getStatus())) {
                req.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toParticipationRequestDto(req));
            }
        }

        requestRepository.saveAll(requests);
        event.setConfirmedRequests(currentConfirmed);
        eventRepository.save(event);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }


    @Override
    @Transactional
    public List<EventFullDto> getEvents(List<Long> userIds, List<String> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Specification<Event> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userIds != null && !userIds.isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(userIds));
            }
            if (states != null && !states.isEmpty()) {
                List<EventState> eventStates = states.stream()
                                                     .map(EventState::valueOf)
                                                     .toList();
                predicates.add(root.get("state").in(eventStates));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (rangeStart != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }

            if (rangeEnd != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        return events.stream()
                     .map(eventMapper::toEventFullDto)
                     .toList();
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

        if (event.getState() == EventState.PUBLISHED && event.getPublishedOn() != null) {
            if (eventDto.getEventDate() != null) {
                LocalDateTime minAllowedDate = event.getPublishedOn().plusHours(1);
                if (eventDto.getEventDate().isBefore(minAllowedDate)) {
                    throw new ConflictException("The date of the event must be no earlier than one hour after publication.");
                }
                event.setEventDate(eventDto.getEventDate());
            }
        }

        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory()).orElseThrow(() -> {
                log.error("Category with ID {} not found", eventDto.getCategory());
                return new NotFoundException("Category not found");
            }));
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(eventDto.getLocation()));
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

        if (eventDto.getStateAction() != null) {
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

        eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("The beginning of the range cannot be later than its end");
        }

        Specification<Event> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

            if (text != null && !text.isBlank()) {
                String pattern = "%%" + text.toLowerCase() + "%%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
                ));
            }

            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            if (paid != null) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), paid));
            }

            if (rangeStart != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }

            if (rangeEnd != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            if (onlyAvailable != null && onlyAvailable) {
                predicates.add(criteriaBuilder.greaterThan(root.get("participantLimit"), 0));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        EventSort eventSort = sort != null ? EventSort.valueOf(sort.toUpperCase()) : null;
        Sort sorting = Sort.unsorted();
        if (eventSort != null) {
            if (eventSort == EventSort.EVENT_DATE) {
                sorting = Sort.by(Sort.Direction.DESC, "eventDate");
            } else if (eventSort == EventSort.VIEWS) {
                sorting = Sort.by(Sort.Direction.DESC, "views");
            }
        }

        Pageable pageable = PageRequest.of(from / size, size, sorting);
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        return eventMapper.toEventShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventPublicById(Long id, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(id).orElseThrow(() -> {
            log.error("Event with ID {} not found", id);
            return new NotFoundException("Event not found");
        });
        if (event.getState() != EventState.PUBLISHED) {
            log.error("Event with ID {} not found", id);
            throw new NotFoundException("Event not found");
        }
        hit(httpServletRequest);
        String startTime = String.valueOf(event.getPublishedOn());
        String endTime = String.valueOf(LocalDateTime.now());

        List<ViewStats> stats = statsClient.stats(startTime, endTime, List.of("/events/" + id), true);
        Long views = stats.getFirst().getHits();
        event.setViews(views);
        return eventMapper.toEventFullDto(event);
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

    private void hit(HttpServletRequest httpServletRequest) {
        HitRequest hitRequest = new HitRequest(
                "main-server",
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        statsClient.hit(hitRequest);
    }
}
