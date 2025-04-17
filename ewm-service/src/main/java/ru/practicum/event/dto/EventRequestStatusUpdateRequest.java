package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
