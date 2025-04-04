package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.CategoryDto;

import java.io.IOException;
import java.util.List;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);

    CategoryDto getOne(Long id);

    List<CategoryDto> getMany(List<Long> ids);

    CategoryDto create(CategoryDto dto);

    CategoryDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    CategoryDto delete(Long id);

    void deleteMany(List<Long> ids);
}
