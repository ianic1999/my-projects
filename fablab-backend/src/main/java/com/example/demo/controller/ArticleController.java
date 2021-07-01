package com.example.demo.controller;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.ArticleException;
import com.example.demo.model.exception.CategoryException;
import com.example.demo.model.exception.UserException;
import com.example.demo.service.ArticleService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<ArticleDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage,
                                                                @RequestParam(required = false) String order,
                                                                @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(articleService.getAll(page, perPage, order == null ? null : order.substring(order.startsWith("-") ? 1 : 0), order != null && !order.startsWith("-"), search))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ArticleDTO>> getById(@PathVariable long id) throws ArticleException {
        return ResponseEntity.ok(
                new Response<>(articleService.getById(id))
        );
    }

    @PostMapping
    public ResponseEntity<Response<ArticleDTO>> add(@RequestParam String title,
                                                    @RequestParam String body,
                                                    @RequestParam MultipartFile image,
                                                    @RequestParam String categoryId) throws CategoryException, IOException, UserException {
        return new ResponseEntity<>(
                new Response<>(articleService.add(title, body, image, Long.parseLong(categoryId), userService.getCurrentUser().getId())),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{articleId}")
    public ResponseEntity<Response<ArticleDTO>> updatePatch(@PathVariable long articleId,
                                                       @RequestParam(required = false) String id,
                                                       @RequestParam(required = false) String title,
                                                       @RequestParam(required = false) String body,
                                                       @RequestParam(required = false) MultipartFile image,
                                                       @RequestParam(required = false) String categoryId) throws CategoryException, ArticleException, IOException {
        return new ResponseEntity<>(
                new Response<>(articleService.update(articleId, title, body, image, categoryId != null ? Long.parseLong(categoryId) : 0)),
                HttpStatus.OK
        );
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<Response<ArticleDTO>> updatePut(@PathVariable long articleId,
                                                       @RequestParam String id,
                                                       @RequestParam String title,
                                                       @RequestParam String body,
                                                       @RequestParam(required = false) MultipartFile image,
                                                       @RequestParam String categoryId) throws CategoryException, ArticleException, IOException {
        return new ResponseEntity<>(
                new Response<>(articleService.update(articleId, title, body, image, categoryId != null ? Long.parseLong(categoryId) : 0)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws IOException, ArticleException {
        articleService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
