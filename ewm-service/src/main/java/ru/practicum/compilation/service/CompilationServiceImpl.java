package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService{
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        List<Long> eventsIds = compilationDto.getEvents();
        List<Event> events = eventRepository.findAllById(eventsIds);
        Compilation compilation = compilationMapper.toCompilation(compilationDto, events);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long id) {
        compilationRepository.findById(id);
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Long id) {
        Compilation existingCompilation = compilationRepository.findById(id).orElseThrow(() -> {
            log.error("Compilation with ID {} not found", id);
            return new NotFoundException("Compilation not found");
        });
        if(updateCompilationDto.getEvents() != null) {
            existingCompilation.setEvents(eventRepository.findAllById(updateCompilationDto.getEvents()));
        }

        if(updateCompilationDto.getTitle() != null) {
            existingCompilation.setTitle(updateCompilationDto.getTitle());
        }
        existingCompilation.setPinned(updateCompilationDto.getPinned());
        return compilationMapper.toCompilationDto(compilationRepository.save(existingCompilation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;

        if(pinned != null) {
            compilations = compilationRepository.findAllByPinnedIs(pinned, PageRequest.of(from / size, size));
        }
        else {
            compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).toList();
        }
        return compilationMapper.toCompilationDto(compilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> {
            log.error("Compilation with ID {} not found", id);
            return new NotFoundException("Compilation not found");
        });

        return compilationMapper.toCompilationDto(compilation);
    }
}
