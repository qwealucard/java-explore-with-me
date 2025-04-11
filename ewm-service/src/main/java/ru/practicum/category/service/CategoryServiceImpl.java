package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);

        if (categoryRepository.existsByName(category.getName())) {
            throw new ConflictException("That name is taken.");
        }

        log.info("create category by admin, category ID:{}", category.getId());
        categoryRepository.save(category);
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id);
        if (!eventRepository.findByCategoryId(id).isEmpty()) {
            throw new ConflictException("You can`t delete this category because it contains one or more events.");
        }

        categoryRepository.deleteById(id);
        log.info("Delete category with ID {} by admin", id);
    }

    public CategoryDto updateCategory(NewCategoryDto newCategoryDto, Long catId) {
        categoryRepository.findById(catId);
        Optional<Category> existingCategory = categoryRepository.findByName(newCategoryDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(catId)) {
            throw new ConflictException("This name is taken");
        }
        Category category = categoryMapper.toCategory(newCategoryDto);
        category.setId(catId);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryMapper.toCategoryDto(categoryRepository.findAll(PageRequest.of(from / size, size)).toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with ID {} not found", id);
            return new NotFoundException("Category not found");
        });

        return categoryMapper.toCategoryDto(category);
    }
}
