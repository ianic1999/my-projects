package com.example.demo.util;

import com.example.demo.model.Article;
import com.example.demo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SitemapGenerator {
    private final ArticleRepository articleRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateSiteMap() throws FileNotFoundException {
        StringBuilder text = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                "  <url>\n" +
                "    <loc>https://www.oklyx.com/</loc>\n" +
                "    <lastmod>" + LocalDate.now() + "</lastmod>\n" +
                "  </url>\n");
        text.append("  <url>\n" + "    <loc>https://www.oklyx.com/blog</loc>\n" + "    <lastmod>").append(LocalDate.now()).append("</lastmod>\n").append("  </url>\n");
        text.append("  <url>\n" + "    <loc>https://www.oklyx.com/contact</loc>\n" + "    <lastmod>").append(LocalDate.now()).append("</lastmod>\n").append("  </url>\n");
        for (Article article : articleRepository.findAll()) {
            text.append("  <url>\n" + "    <loc>https://www.oklyx.com/blog/").append(article.getAlias()).append("</loc>\n").append("    <lastmod>").append(article.getUpdatedAt().toLocalDate()).append("</lastmod>\n").append("  </url>\n");
        }
        text.append("</urlset>");
        File file = new File("src/main/resources/sitemap.xml");
        PrintWriter writer = new PrintWriter(file);
        writer.write(text.toString());
        writer.close();
    }
}
