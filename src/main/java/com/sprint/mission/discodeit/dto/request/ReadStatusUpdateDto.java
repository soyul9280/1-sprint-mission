package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;

@Getter
public class ReadStatusUpdateDto {
    @NotNull(message = "새로 읽은 시간은 필수입니다.")
    private Instant newLastReadAt;
}
