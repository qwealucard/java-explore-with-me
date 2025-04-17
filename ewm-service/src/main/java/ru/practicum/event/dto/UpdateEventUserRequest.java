package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.StateAction;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
