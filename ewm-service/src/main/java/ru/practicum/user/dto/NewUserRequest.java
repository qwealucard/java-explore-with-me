package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class NewUserRequest {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String name;
}
