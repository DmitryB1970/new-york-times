package com.javacademy.new_york_times.repository;

import com.javacademy.new_york_times.entity.NewsEntity;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * НЕЛЬЗЯ МЕНЯТЬ ЭТОТ КЛАСС
 */
@Component
public class NewsRepository {
    private final AtomicInteger counter = new AtomicInteger();
    @Getter
    private final Map<Integer, NewsEntity> data = new TreeMap<>();

    @PostConstruct
    public void init() {
        IntStream.range(0, 55).boxed().forEach(e -> save(createNews()));
    }

    private NewsEntity createNews() {
        return NewsEntity.builder()
                .text("Today is Groundhog Day #%s".formatted(counter.get() + 1))
                .title("News #%s".formatted(counter.get() + 1))
                .author("Molodyko Yuri")
                .build();
    }

    public void save(NewsEntity entity) {
        if (entity.getNumber() != null) {
            throw new RuntimeException("News with number %s - already exists".formatted(entity.getNumber()));
        }
        entity.setNumber(counter.addAndGet(1));

        data.put(entity.getNumber(), entity);
    }

    @SneakyThrows
    public List<NewsEntity> findAll() {
        Thread.sleep(5000);
        return new ArrayList<>(data.values());
    }

    @SneakyThrows
    public Optional<NewsEntity> findByNumber(Integer number) {
        Thread.sleep(5000);
        return Optional.ofNullable(data.get(number));
    }

    public boolean deleteByNumber(Integer number) {
        return data.remove(number) != null;
    }

    public void update(NewsEntity updateEntity) {
        if (!data.containsKey(updateEntity.getNumber())) {
            throw new RuntimeException("News with number %s - is not exists".formatted(updateEntity.getNumber()));
        }
        data.put(updateEntity.getNumber(), updateEntity);
    }
}
