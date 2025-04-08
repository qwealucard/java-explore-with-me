package ru.practicum.event.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

public class EventFullDto {
    private Long id;

    private String annotation;
    private boolean paid;
    private String title;
    private LocalDateTime eventDate;
    private String description;
    private boolean requestModeration;
    private Long participantLimit;
    private LocalDateTime publishedOn;
    private LocalDateTime createdOn;
    private CategoryDto category;
    private UserShortDto initiator;
    private LocationDto location;
    private EventState state;
    private Long confirmedRequests;
    private Long views;
}
