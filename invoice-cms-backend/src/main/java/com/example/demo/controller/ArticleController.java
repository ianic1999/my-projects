package com.example.demo.controller;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.Response;
import com.example.demo.model.exception.ArticleException;
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
    private final UserService userService;
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<ArticleDTO>> getAll(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "15") int perPage) {
        return ResponseEntity.ok(
                new PaginatedResponse<>(articleService.get(page, perPage))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ArticleDTO>> getById(@PathVariable long id) throws ArticleException {
        return ResponseEntity.ok(
                new Response<>(articleService.getById(id))
        );
    }

    @GetMapping("/alias/{alias}")
    public ResponseEntity<Response<ArticleDTO>> getByAlias(@PathVariable String alias) throws ArticleException {
        return ResponseEntity.ok(
                new Response<>(articleService.getByAlias(alias))
        );
    }

    @PostMapping
    public ResponseEntity<Response<ArticleDTO>> add(@RequestParam String title,
                                                    @RequestParam String description,
                                                    @RequestParam String body,
                                                    @RequestParam MultipartFile image,
                                                    @RequestParam(required = false) String youtubeLink,
                                                    @RequestParam String keywords) throws UserException, IOException {
        return new ResponseEntity<>(
                new Response<>(articleService.add(title, description, body, image, keywords, youtubeLink, userService.getCurrentUser())),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<Response<ArticleDTO>> updatePut(@PathVariable long articleId,
                                                          @RequestParam long id,
                                                          @RequestParam String title,
                                                          @RequestParam String description,
                                                          @RequestParam String body,
                                                          @RequestParam MultipartFile image,
                                                          @RequestParam(required = false) String youtubeLink,
                                                          @RequestParam String keywords) throws UserException, IOException, ArticleException {
        return ResponseEntity.ok(
            new Response<>(articleService.update(articleId, title, description, body, image, keywords, youtubeLink, userService.getCurrentUser()))
        );
    }

    @PatchMapping("/{articleId}")
    public ResponseEntity<Response<ArticleDTO>> updatePatch(@PathVariable long articleId,
                                                          @RequestParam long id,
                                                          @RequestParam(required = false) String title,
                                                          @RequestParam(required = false) String description,
                                                          @RequestParam(required = false) String body,
                                                          @RequestParam(required = false) MultipartFile image,
                                                          @RequestParam(required = false) String keywords,
                                                            @RequestParam(required = false) String youtubeLink) throws UserException, IOException, ArticleException {
        return ResponseEntity.ok(
                new Response<>(articleService.update(articleId, title, description, body, image, keywords, youtubeLink, userService.getCurrentUser()))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) throws UserException, IOException, ArticleException {
        articleService.remove(id, userService.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

}
