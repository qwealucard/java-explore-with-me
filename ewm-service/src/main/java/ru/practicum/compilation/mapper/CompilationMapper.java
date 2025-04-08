package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.request.model.Request;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    Compilation toCompilation(NewCompilationDto compilationDto);

    CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(target = "events.id", source = "events")
    Compilation toCompilation(UpdateCompilationDto updateCompilationDto);
    List<CompilationDto> toCompilationDto(List<Compilation> compilations);
}
