package com.example.demo.service.impl;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Article;
import com.example.demo.model.enums.UserRole;
import com.example.demo.model.exception.ArticleException;
import com.example.demo.model.exception.UserException;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.service.ArticleService;
import com.example.demo.util.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final AwsS3Service awsService;

    @Override
    public Page<ArticleDTO> get(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return articleRepository.findAll(pageable).map(Article::toDTO);
    }

    @Override
    public ArticleDTO getById(long id) throws ArticleException {
        return articleRepository.findById(id).orElseThrow(() -> new ArticleException("Article with id " + id + " doesn't exist")).toDTO();
    }

    @Override
    public ArticleDTO getByAlias(String alias) throws ArticleException {
        return articleRepository.findByAlias(alias).orElseThrow(() -> new ArticleException("Article with alias " + alias + " doesn't exist")).toDTO();
    }

    @Override
    @Transactional
    public ArticleDTO add(String title, String description, String body, MultipartFile image, String keywords, String youtubeLink, UserDTO currentUser) throws UserException, IOException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Only admin users can create articles");
        Article article = Article.builder()
                .title(title)
                .alias(title.toLowerCase().replaceAll("[^a-zA-Z0-9]", "-"))
                .description(description)
                .body(body)
                .keywords(keywords)
                .youtubeLink(youtubeLink)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        article = articleRepository.save(article);
        String imagePath = image != null ? awsService.uploadFile(image, "articles/" + article.getId() + "/", image.getOriginalFilename()) : null;
        article.setImage(imagePath);
        return article.toDTO();
    }

    @Override
    @Transactional
    public ArticleDTO update(long id, String title, String description, String body, MultipartFile image, String keywords, String youtubeLink, UserDTO currentUser) throws UserException, ArticleException, IOException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Only admin users can update articles");
        Article article = articleRepository.findById(id).orElseThrow(() -> new ArticleException("Article with id " + id + " doesn't exist"));
        String imagePath = null;
        if (image != null) {
            if (article.getImage() != null)
                awsService.deleteFile("articles/" + article.getId() + "/" + article.getImage());
            imagePath = awsService.uploadFile(image, "articles/" + article.getId() + "/", image.getOriginalFilename());
        }
        if (title != null) {
            article.setTitle(title);
            article.setAlias(title.toLowerCase().replaceAll("[^a-zA-Z0-9]", "-"));
        }
        if (description != null) {
            article.setDescription(description);
        }
        if (body != null) {
            article.setBody(body);
        }
        if (imagePath != null) {
            article.setImage(imagePath);
        }
        if (keywords != null) {
            article.setKeywords(keywords);
        }
        if (youtubeLink != null) {
            article.setYoutubeLink(youtubeLink);
        }
        article.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return article.toDTO();
    }

    @Override
    public void remove(long id, UserDTO currentUser) throws ArticleException, UserException {
        if (!currentUser.getRole().equals(UserRole.ADMIN.getKey()))
            throw new UserException("Only admin users can delete articles");
        Article article = articleRepository.findById(id).orElseThrow(() -> new ArticleException("Article with id " + id + " doesn't exist"));
        if (article.getImage() != null)
            awsService.deleteFile("articles/" + article.getId() + "/" + article.getImage());
        articleRepository.deleteById(id);
    }
}
