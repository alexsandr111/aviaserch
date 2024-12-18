package com.example.demo1111;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public void savePost(BlogPost post) {
        blogPostRepository.save(post); // Сохраняем пост в базе
    }

    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll(); // Получаем все посты
    }

    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id).orElse(null);
    }

    public void deletePost(Long id) {
        blogPostRepository.deleteById(id);
    }

    // Новый метод для поиска записей
    public List<BlogPost> searchBlogPosts(String query, String criteria) {
        LocalDate queryDate = null;
        String titleText = null;
        String contentText = null;

        // Разделяем строку поиска на слова
        String[] parts = query.split(" ");

        // Определяем дату (если есть)
        if (parts.length > 0) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                queryDate = LocalDate.parse(parts[0], formatter);
            } catch (DateTimeParseException e) {
                queryDate = null; // Если это не дата, сбрасываем
            }
        }

        // Логика в зависимости от критерия
        switch (criteria) {
            case "date":
                // Для поиска по дате ничего, кроме даты, не нужно
                break;

            case "title":
                if (parts.length > 0) {
                    titleText = String.join(" ", parts);
                }
                break;

            case "content":
                if (parts.length > 0) {
                    contentText = String.join(" ", parts);
                }
                break;

            case "dateAndTitle":
                if (queryDate != null && parts.length > 1) {
                    titleText = parts[1];
                }
                break;

            case "dateAndContent":
                if (queryDate != null && parts.length > 1) {
                    contentText = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                }
                break;

            case "all":
                if (queryDate != null && parts.length > 2) {
                    titleText = parts[1];
                    contentText = String.join(" ", java.util.Arrays.copyOfRange(parts, 2, parts.length));
                }
                break;

            default:
                return List.of(); // Некорректный критерий
        }

        // Логирование для диагностики
        System.out.println("Критерий: " + criteria);
        System.out.println("Дата: " + queryDate);
        System.out.println("Название: " + titleText);
        System.out.println("Текст записи: " + contentText);

        // Проверяем критерии
        if (criteria.equals("date") && (queryDate == null || titleText != null || contentText != null)) {
            return List.of();
        }
        if (criteria.equals("title") && (titleText == null || queryDate != null || contentText != null)) {
            return List.of();
        }
        if (criteria.equals("content") && (contentText == null || queryDate != null || titleText != null)) {
            return List.of();
        }
        if (criteria.equals("dateAndTitle") && (queryDate == null || titleText == null || contentText != null)) {
            return List.of();
        }
        if (criteria.equals("dateAndContent") && (queryDate == null || contentText == null || titleText != null)) {
            return List.of();
        }
        if (criteria.equals("all") && (queryDate == null || titleText == null || contentText == null)) {
            return List.of();
        }

        // Вызов репозитория в зависимости от критерия
        if ("all".equals(criteria)) {
            // Для критерия all передаём все параметры
            return blogPostRepository.searchBlogPosts(
                    null,  // query не используется для `all`
                    queryDate,
                    criteria,
                    titleText,
                    contentText
            );
        } else {
            // Для других критериев используем общий параметр query
            String queryForRepo = contentText != null ? contentText : titleText;
            return blogPostRepository.searchBlogPosts(queryForRepo, queryDate, criteria, null, null);
        }
    }


}
