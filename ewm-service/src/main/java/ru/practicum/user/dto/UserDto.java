package ru.practicum.user.dto;

import jakarta.validation.constraints.NotNull;

public class UserDto {

    @NotNull
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String name;
}
