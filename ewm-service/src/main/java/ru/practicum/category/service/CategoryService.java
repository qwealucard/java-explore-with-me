package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto categoryDto);

    void deleteCategory(Long id);

    CategoryDto updateCategory(NewCategoryDto categoryDto, Long id);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long id);
}
