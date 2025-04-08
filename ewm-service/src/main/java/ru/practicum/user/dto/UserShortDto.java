package ru.practicum.user.dto;

import jakarta.validation.constraints.NotNull;

public class UserShortDto {
    @NotNull
    private Long id;

    @NotNull
    private String name;
}
