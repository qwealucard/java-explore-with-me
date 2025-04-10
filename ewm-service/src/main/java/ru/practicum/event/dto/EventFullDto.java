package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventFullDto {
    private Long id;

    private String annotation;
    private boolean paid;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
