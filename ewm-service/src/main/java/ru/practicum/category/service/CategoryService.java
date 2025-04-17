package ru.practicum.category.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

@Transactional
public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto categoryDto);

    void deleteCategory(Long id);

    CategoryDto updateCategory(NewCategoryDto categoryDto, Long id);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long id);
}
