package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.StateAction;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventUserRequest {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    private String description;

    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
