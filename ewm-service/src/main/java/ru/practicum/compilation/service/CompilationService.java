package ru.practicum.compilation.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    @Transactional
    CompilationDto createCompilation(NewCompilationDto compilationDto);

    @Transactional
    void deleteCompilation(Long id);

    @Transactional
    CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Long id);

    @Transactional(readOnly = true)
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    @Transactional(readOnly = true)
    CompilationDto getCompilationById(Long id);
}
