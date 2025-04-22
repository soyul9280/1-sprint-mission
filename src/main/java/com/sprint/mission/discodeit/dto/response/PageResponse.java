package com.sprint.mission.discodeit.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponse <T>{
    private final List<T> content;
    private final int number;
    private final int size;
    private final Long totalElements;
    private final boolean hasNext;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.hasNext = page.hasNext();
    }

    public PageResponse(List<T> content, int number, int size, Long totalElements, boolean hasNext) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
}
