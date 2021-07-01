package com.example.demo.service;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.model.exception.CategoryException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    Page<CategoryDTO> getAll(int page, int size, String order, boolean ascending, String search);
    CategoryDTO getById(long id) throws CategoryException;
    CategoryDTO add(CategoryDTO dto);
    CategoryDTO update(CategoryDTO dto) throws CategoryException;
    void remove(long id) throws CategoryException;
}
