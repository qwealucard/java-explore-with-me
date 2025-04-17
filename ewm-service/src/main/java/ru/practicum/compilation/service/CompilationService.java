package ru.practicum.compilation.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

@Transactional
public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long id);

    CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Long id);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long id);
}
