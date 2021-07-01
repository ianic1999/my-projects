package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.CategoryException;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<CategoryDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "15") int perPage,
                                                                 @RequestParam(required = false) String order,
                                                                 @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(categoryService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CategoryDTO>> getById(@PathVariable long id) throws CategoryException {
        return ResponseEntity.ok(
                new Response<>(categoryService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<CategoryDTO>> add(@RequestBody CategoryDTO dto) {
        return new ResponseEntity<>(
                new Response<>(categoryService.add(dto)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Response<CategoryDTO>> update(@PathVariable long categoryId,
                                                        @RequestBody CategoryDTO dto) throws CategoryException {
        return ResponseEntity.ok(
                new Response<>(categoryService.update(dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws CategoryException {
        categoryService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
