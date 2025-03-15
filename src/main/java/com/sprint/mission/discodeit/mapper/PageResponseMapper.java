package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageResponseMapper {
    public <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(page);
    }
    public <T> PageResponse<T> fromSlice(Slice<T> slice, List<T> content) {
        return new PageResponse<>(
                content,
                slice.getNumber(),
                slice.getSize(),
                null,
                slice.hasNext()

        );
    }
}
