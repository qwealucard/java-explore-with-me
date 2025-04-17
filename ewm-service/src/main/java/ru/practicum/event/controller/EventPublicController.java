package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPublicFilter;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Valid
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false)
                                         @Size(min = 1, max = 7000) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                         @RequestParam(defaultValue = "10") Integer size, HttpServletRequest httpServletRequest) {

        EventPublicFilter filter = EventPublicFilter.builder()
                                                    .text(text)
                                                    .categories(categories)
                                                    .paid(paid)
                                                    .rangeStart(rangeStart)
                                                    .rangeEnd(rangeEnd)
                                                    .onlyAvailable(onlyAvailable)
                                                    .sort(sort)
                                                    .from(from)
                                                    .size(size)
                                                    .httpServletRequest(httpServletRequest)
                                                    .build();

        return eventService.getEventsPublic(filter);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(eventService.getEventPublicById(eventId, httpServletRequest), HttpStatus.OK);
    }
}
