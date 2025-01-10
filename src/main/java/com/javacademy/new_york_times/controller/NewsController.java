package com.javacademy.new_york_times.controller;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageDto;
import com.javacademy.new_york_times.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * Сделать 7 операций внутри контроллера.
 * 1. Создание новости. Должно чистить кэш.
 * 2. Удаление новости по id. Должно чистить кэш.
 * 3. Получение новости по id. Должно быть закэшировано.
 * 4. Получение всех новостей (новости должны отдаваться порциями по 10 штук). Должно быть закэшировано.
 * 5. Обновление новости по id. Должно чистить кэш.
 * 6. Получение текста конкретной новости.
 * 7. Получение автора конкретной новости.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController {

    private static final int MAX_PAGE_SIZE = 10;
    private final NewsService newsService;


    //1. Создание новости
    @PostMapping
    @CacheEvict(value = "news", allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody NewsDto newsDto) {
        newsService.save(newsDto);
    }

    //2. Удаление новости по id
    @DeleteMapping("/{id}")
    @CacheEvict(value = "news", allEntries = true)
    public boolean deleteById(@PathVariable Integer id) {
        return newsService.deleteByNumber(id);
    }

    //3. Получение новости по id
    @GetMapping("/{id}")
    @Cacheable(value = "news")
    public NewsDto getById(@PathVariable Integer id) {
        return newsService.findByNumber(id);
    }

    //4. Получение всех новостей(порциями по 10 штук)
    @GetMapping
    @Cacheable(value = "news")
    public PageDto<NewsDto> getAll(int pageNumber) {
        List<NewsDto> dtos = newsService.findAll().stream()
                .sorted(Comparator.comparing(NewsDto::getNumber))
                .skip(MAX_PAGE_SIZE * pageNumber)
                .limit(MAX_PAGE_SIZE)
                .toList();
        Integer totalNewsCount = dtos.size();
        int totalPages = totalNewsCount / MAX_PAGE_SIZE;
        return new PageDto<>(dtos, totalPages, pageNumber, MAX_PAGE_SIZE, totalNewsCount, dtos.size());
    }

    //5. Обновление новости по id
    @PatchMapping
    @CacheEvict(value = "news", allEntries = true)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void updateDto(@RequestBody NewsDto newsDto) {
        newsService.update(newsDto);
    }

    //6. Получение текста конкретной новости.
    @GetMapping("/text")
    public ResponseEntity<?> getNewsText(@RequestParam Integer newsNumber) {
        return ResponseEntity.ok(newsService.getNewsText(newsNumber));
    }

    //7. Получение автора конкретной новости.
    @GetMapping("/author")
    public ResponseEntity<?> getNewsAuthor(@RequestParam Integer newsNumber) {
        return ResponseEntity.ok(newsService.getNewsAuthor(newsNumber));
    }
}
