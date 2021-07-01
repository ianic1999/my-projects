package com.example.demo.service.impl;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.model.Category;
import com.example.demo.model.exception.CategoryException;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    @Override
    public Page<CategoryDTO> getAll(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return categoryRepository.findByTitleContainsIgnoreCase(search, pageable).map(Category::toDTO);
    }

    @Override
    public CategoryDTO getById(long id) throws CategoryException {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryException("Category with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public CategoryDTO add(CategoryDTO dto) {
        Category category = Category.builder()
                .title(dto.getTitle())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return categoryRepository.save(category).toDTO();
    }

    @Override
    @Transactional
    public CategoryDTO update(CategoryDTO dto) throws CategoryException {
        Category category = categoryRepository.findById(dto.getId()).orElseThrow(() -> new CategoryException("Category with id " + dto.getId() + " doesn't exist"));
        if (dto.getTitle() != null)
            category.setTitle(dto.getTitle());
        category.setUpdatedAt(LocalDateTime.now());
        return category.toDTO();
    }

    @Override
    public void remove(long id) throws CategoryException {
        long articles = articleRepository.findAll()
                .stream()
                .filter(article -> article.getCategory().getId() == id)
                .count();
        if (articles > 0)
            throw new CategoryException("This category is used is some articles.");
        categoryRepository.deleteById(id);
    }
}
