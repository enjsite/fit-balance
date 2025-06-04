package ru.enjy.fit_balance.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.enjy.fit_balance.model.dto.CategoryDto;
import ru.enjy.fit_balance.model.entity.Category;
import ru.enjy.fit_balance.model.mapper.CategoryMapper;
import ru.enjy.fit_balance.repository.CategoryRepository;
import ru.enjy.fit_balance.service.CategoryService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper;

    @Override
    public Page<CategoryDto> getAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toCategoryDto);
    }

    @Override
    public CategoryDto getOne(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryMapper.toCategoryDto(categoryOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<CategoryDto> getMany(List<Long> ids) {
        List<Category> categories = categoryRepository.findAllById(ids);
        return categories.stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        Category resultCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(resultCategory);
    }

    @Override
    public CategoryDto patch(Long id, JsonNode patchNode) throws IOException {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        objectMapper.readerForUpdating(categoryDto).readValue(patchNode);
        categoryMapper.updateWithNull(categoryDto, category);

        Category resultCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(resultCategory);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Category> categories = categoryRepository.findAllById(ids);

        for (Category category : categories) {
            CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
            objectMapper.readerForUpdating(categoryDto).readValue(patchNode);
            categoryMapper.updateWithNull(categoryDto, category);
        }

        List<Category> resultCategories = categoryRepository.saveAll(categories);
        return resultCategories.stream()
                .map(Category::getId)
                .toList();
    }

    @Override
    public CategoryDto delete(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            categoryRepository.delete(category);
        }
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        categoryRepository.deleteAllById(ids);
    }
}
