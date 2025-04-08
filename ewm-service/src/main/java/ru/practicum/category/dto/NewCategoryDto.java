package ru.practicum.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCategoryDto {
    @NotNull
    private String name;
}
