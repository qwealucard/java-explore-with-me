package ru.practicum.compilation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateCompilationDto {

    private List<Long> events;
    private boolean pinned;
    private String title;
}
