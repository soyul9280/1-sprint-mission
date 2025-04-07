package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusCreateDto {
    @NotNull(message = "유저 ID는 필수입니다.")
    private UUID userId;
    @NotNull(message = "활동 시간은 필수입니다.")
    private Instant lastAttendAt;
}
