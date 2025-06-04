package ru.enjy.fit_balance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.enjy.fit_balance.model.dto.CategoryDto;
import ru.enjy.fit_balance.service.CategoryService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public PagedModel<CategoryDto> getAll(Pageable pageable) {
        Page<CategoryDto> categoryDtos = categoryService.getAll(pageable);
        return new PagedModel<>(categoryDtos);
    }

    @GetMapping("/{id}")
    public CategoryDto getOne(@PathVariable Long id) {
        return categoryService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<CategoryDto> getMany(@RequestParam List<Long> ids) {
        return categoryService.getMany(ids);
    }

    @PostMapping
    public CategoryDto create(@RequestBody CategoryDto dto) {
        return categoryService.create(dto);
    }

    @PatchMapping("/{id}")
    public CategoryDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return categoryService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return categoryService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public CategoryDto delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        categoryService.deleteMany(ids);
    }
}
