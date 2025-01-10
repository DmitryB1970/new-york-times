package com.javacademy.new_york_times.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageDto<T> {
    private List<T> news;
    private Integer totalPages;
    private Integer currentPage;
    private Integer maxPageSize;
    private Integer totalNewsCount;
    private Integer newsOnPage;

}
