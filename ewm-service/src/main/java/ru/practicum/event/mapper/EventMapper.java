package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryEntity")
    @Mapping(target = "initiator.id", source = "initiatorId")
    Event toEvent(NewEventDto eventDto, Category categoryEntity, Long initiatorId);

    EventFullDto toEventFullDto(Event event);
    EventShortDto toEventShortDto(Event event);
    List<EventFullDto> toEventFullDto(List<Event> events);
}
