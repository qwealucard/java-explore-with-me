package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
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
public class RequestServiceImpl implements RequestService{

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;


    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequests(Long id) {
        userRepository.findById(id);
        return requestMapper.toParticipationRequestDto(requestRepository.findByRequesterId(id));
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with ID {} not found", userId);
            return new NotFoundException("User not found");
        });
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event not found");
        });

        if(requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("You can't add a repeat request");
        }

        if(userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("The initiator of the event cannot add a request to participate in his event");
        }

        if(event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("This event is unpublished");
        }

        if(event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("The event has reached the limit of participation requests");
        }

        if(!event.getRequestModeration()) {
            event.setState(EventState.PUBLISHED);
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(RequestStatus.PENDING);

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() -> {
            log.error("Request with ID {} not found", requestId);
            return new NotFoundException("Request not found");
        });

//        if (!request.getRequester().getId().equals(userId)) {
//            throw new ValidationException("Only the creator can cancel the request.");
//        }
//        if (request.getStatus() == RequestStatus.CANCELED) {
//            throw new ConflictException("Request has already been canceled");
//        }

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }


}
