package ru.practicum.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.RequestStatus;

import java.time.LocalDateTime;

public class ParticipationRequestDto {
    @Past
    @NotBlank
    private LocalDateTime created;

    @NotNull
    private Event event;

    @NotNull
    private Long id;

    @NotNull
    private Long requester;

    @NotNull
    private RequestStatus status;

}
