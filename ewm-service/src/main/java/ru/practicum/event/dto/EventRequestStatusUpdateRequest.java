package ru.practicum.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
