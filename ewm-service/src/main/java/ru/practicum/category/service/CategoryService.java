package ru.practicum.category.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    @Transactional
    CategoryDto createCategory(NewCategoryDto categoryDto);

    @Transactional
    void deleteCategory(Long id);

    @Transactional
    CategoryDto updateCategory(NewCategoryDto categoryDto, Long id);

    @Transactional(readOnly = true)
    List<CategoryDto> getCategories(Integer from, Integer size);

    @Transactional(readOnly = true)
    CategoryDto getCategoryById(Long id);
}
