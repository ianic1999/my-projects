package com.example.demo.service.impl;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.model.Article;
import com.example.demo.model.Category;
import com.example.demo.model.User;
import com.example.demo.model.exception.ArticleException;
import com.example.demo.model.exception.CategoryException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ArticleService;
import com.example.demo.util.FileSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ArticleDTO> getAll(int page, int size, String order, boolean ascending, String search) {
        Pageable pageable = order == null ? PageRequest.of(page - 1, size)
                : ascending ? PageRequest.of(page - 1, size, Sort.by(order).ascending())
                    : PageRequest.of(page - 1, size, Sort.by(order).descending());
        return articleRepository.findByTitleContainsIgnoreCase(search, pageable).map(Article::toDTO);
    }

    @Override
    public ArticleDTO getById(long id) throws ArticleException {
        return articleRepository.findById(id).orElseThrow(() -> new ArticleException("Article with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public ArticleDTO add(String title, String body, MultipartFile image, long categoryId, long userId) throws CategoryException, IOException, UserException {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryException("Category with id " + categoryId + " doesn't exist"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User with id " + userId + " doesn't exist"));
        String imagePath = FileSaver.save(image, "articles", image.getOriginalFilename());
        Article article = Article.builder()
                .title(title)
                .body(body)
                .image(imagePath)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(user)
                .build();
        return articleRepository.save(article).toDTO();
    }

    @Override
    @Transactional
    public ArticleDTO update(long id, String title, String body, MultipartFile image, long categoryId) throws CategoryException, ArticleException, IOException {
        Category category = categoryId != 0 ? categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryException("Category with id " + categoryId + " doesn't exist")) : null;
        Article article = articleRepository.findById(id).orElseThrow(() -> new ArticleException("Article with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            imagePath = FileSaver.save(image, "articles", image.getOriginalFilename());
            if (imagePath != null && (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")))
                FileSaver.delete(article.getImage());
        }
        if (title != null)
            article.setTitle(title);
        if (body != null)
            article.setBody(body);
        if (imagePath != null)
            article.setImage(imagePath);
        if (category != null)
            article.setCategory(category);
        article.setUpdatedAt(LocalDateTime.now());
        return article.toDTO();
    }

    @Override
    public void remove(long id) throws ArticleException, IOException {
        Article article = articleRepository.findById(id).orElseThrow(() -> new ArticleException("Article with id " + id + " doesn't exist"));
        FileSaver.delete(article.getImage());
        articleRepository.deleteById(id);
    }
}
